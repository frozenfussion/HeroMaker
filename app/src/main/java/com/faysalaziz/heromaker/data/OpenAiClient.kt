package com.faysalaziz.heromaker.data

import android.util.Base64
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** Anything that goes wrong talking to OpenAI. [message] is safe to show to the user. */
class OpenAiException(message: String, val httpCode: Int? = null) : Exception(message)

/** Minimal OpenAI client built on the standard library, so there are no extra dependencies to learn. Call from a background thread. */
object OpenAiClient {
    private const val BASE_URL = "https://api.openai.com/v1"
    private const val CONNECT_TIMEOUT_MS = 20_000
    private const val IMAGE_READ_TIMEOUT_MS = 240_000
    private const val MODELS_READ_TIMEOUT_MS = 30_000

    /** Asks OpenAI which models this key can use and keeps only the image models, newest name first. */
    fun listImageModels(apiKey: String): List<String> {
        val body = request("GET", "/models", apiKey, null, MODELS_READ_TIMEOUT_MS)
        val data = try {
            JSONObject(body).getJSONArray("data")
        } catch (e: JSONException) {
            throw OpenAiException("OpenAI sent a reply the app could not read.")
        }
        val ids = (0 until data.length()).map { data.getJSONObject(it).optString("id") }
        return ids.filter { it.startsWith("gpt-image") }.sortedDescending()
    }

    /** Generates one image and returns its bytes (PNG by default). */
    fun generateImage(apiKey: String, model: String, prompt: String, size: String, quality: String): ByteArray {
        val payload = JSONObject()
            .put("model", model)
            .put("prompt", prompt)
            .put("n", 1)
            .put("size", size)
            .put("quality", quality)
            .toString()
        val body = request("POST", "/images/generations", apiKey, payload, IMAGE_READ_TIMEOUT_MS)
        try {
            val item = JSONObject(body).getJSONArray("data").getJSONObject(0)
            val b64 = item.optString("b64_json")
            if (b64.isNotEmpty()) return Base64.decode(b64, Base64.DEFAULT)
            val url = item.optString("url")
            if (url.isNotEmpty()) return download(url)
        } catch (e: JSONException) {
            // fall through
        }
        throw OpenAiException("OpenAI did not return an image.")
    }

    private fun request(method: String, path: String, apiKey: String, json: String?, readTimeoutMs: Int): String {
        val connection = (URL(BASE_URL + path).openConnection() as HttpURLConnection)
        try {
            connection.requestMethod = method
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = readTimeoutMs
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Accept", "application/json")
            if (json != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) throw OpenAiException(errorMessage(code, text), code)
            return text
        } catch (e: OpenAiException) {
            throw e
        } catch (e: IOException) {
            throw OpenAiException("Could not reach OpenAI. Check your internet connection and try again.")
        } finally {
            connection.disconnect()
        }
    }

    private fun download(url: String): ByteArray {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = MODELS_READ_TIMEOUT_MS
            return connection.inputStream.use { it.readBytes() }
        } catch (e: IOException) {
            throw OpenAiException("The image was created but could not be downloaded.")
        } finally {
            connection.disconnect()
        }
    }

    private fun errorMessage(code: Int, body: String): String {
        val fromApi = try {
            JSONObject(body).optJSONObject("error")?.optString("message").orEmpty()
        } catch (e: JSONException) {
            ""
        }
        return when {
            code == 401 -> "OpenAI rejected the API key. Check it in Settings."
            code == 429 -> fromApi.ifEmpty { "Too many requests, or your OpenAI credit has run out." }
            fromApi.isNotEmpty() -> fromApi
            else -> "OpenAI returned an error (HTTP $code)."
        }
    }
}
