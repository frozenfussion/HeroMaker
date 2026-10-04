package com.faysalaziz.heromaker.data

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Saves a generated image into the phone's gallery, in Pictures/HeroMaker. */
object GallerySaver {
    private const val FOLDER = "HeroMaker"

    /** Returns true on success. Call from a background thread. */
    fun savePng(context: Context, bytes: ByteArray): Boolean {
        val name = "hero_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".png"
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveWithMediaStore(context, bytes, name)
            else saveLegacy(context, bytes, name)
            true
        } catch (e: IOException) {
            false
        } catch (e: SecurityException) {
            false
        }
    }

    private fun saveWithMediaStore(context: Context, bytes: ByteArray, name: String) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$FOLDER")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IOException("Could not create the image entry.")
        try {
            resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: throw IOException("Could not open the image entry.")
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } catch (e: IOException) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    /** Android 9 (API 28) has no scoped storage, so write the file and ask the media scanner to index it. */
    @Suppress("DEPRECATION")
    private fun saveLegacy(context: Context, bytes: ByteArray, name: String) {
        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FOLDER)
        if (!folder.exists() && !folder.mkdirs()) throw IOException("Could not create the folder.")
        val file = File(folder, name)
        file.outputStream().use { it.write(bytes) }
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/png"), null)
    }
}
