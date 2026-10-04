# Hero Maker

Build your own superhero, one choice at a time, and watch an AI paint them.

Hero Maker is a fun Android app. You walk through a short wizard (what are they, what can they do, what do they wear, how should it look), and the app turns your choices into a prompt and asks OpenAI's image models to draw your hero. You can save the result straight to your phone's gallery.

> **This app is a training project.** It was built with [Claude Code](https://claude.com/claude-code) as a hands-on example for the students of **Faysal Aziz**, to show how to plan, design and build a real app by working with Claude Code. More from Faysal Aziz at **[faysalaziz.com](https://faysalaziz.com)**.

## What you can do

- Pick the hero's **nature** (biological or non-biological), **gender**, **species or machine type**, **powers**, **background**, **personality** and **outfit & gear** (cape, hood, hijab, helmet and more).
- Choose an **art style** from 10 looks (anime, cyberpunk, Arcane-style painterly, comic book, pixel art, watercolor, dark fantasy, retro 80s, realistic, chibi), each shown with a sample picture.
- Pick a **setting** and a **pose**, then add your own **remarks**: a name, age, build, hair, costume colours, anything.
- Tap **Surprise** to randomise everything.
- Check the **review** screen. You can even read the exact prompt that will be sent.
- Generate one image, then **save it to your gallery** (`Pictures/HeroMaker`).
- In **Settings**, enter your OpenAI API key, choose the image model, the image size and the quality.

## How to use it

1. **Get an OpenAI API key.** Create an account at [platform.openai.com](https://platform.openai.com), add some API credit, and create a key. Image generation is billed by OpenAI to your account, and larger sizes and higher quality cost more.
2. **Open the app and tap Settings.** Paste your key. It is stored encrypted on your phone (Android Keystore) and is only ever sent to OpenAI.
3. **Pick a model.** Tap *Refresh list from OpenAI* to load the image models your key can use, then choose one. OpenAI retires models from time to time, so refresh if one stops working. Choose a size and quality, then tap *Save settings*.
4. **Make a hero.** Work through the steps. Back and Edit let you change your mind, and Surprise fills everything in for you.
5. **Generate.** On the review screen, tap *Generate hero* and wait up to a minute.
6. **Save.** Tap *Save* to put the picture in your gallery, *Retry* for another take with the same choices, or *Edit* to change them.

## Open it in Android Studio

1. Install the latest stable [Android Studio](https://developer.android.com/studio).
2. **File > New > Project from Version Control**, paste `https://github.com/frozenfussion/HeroMaker.git`, and click **Clone**.
3. Let Gradle sync finish. Android Studio downloads the Android SDK parts it needs.
4. Pick a phone (a real one with USB debugging, or an emulator) and click **Run**.

The app supports Android 9 (API 28) and newer.

## How it is built

- **Kotlin** and **Jetpack Compose**, one activity, no extra UI libraries.
- `app/src/main/java/com/faysalaziz/heromaker/`
  - `data/Options.kt`: every option the wizard offers. Edit these lists to change the choices.
  - `data/PromptBuilder.kt`: turns your choices into the prompt.
  - `data/OpenAiClient.kt`: talks to OpenAI (list image models, generate an image).
  - `data/SecureStore.kt`: keeps the API key encrypted with the Android Keystore.
  - `data/GallerySaver.kt`: saves images to the gallery.
  - `HeroViewModel.kt`: the app's state.
  - `ui/`: the screens and the "Gold Circuit" theme.
- `design/`: the logo, the icon options and the art-style sample images.
- Unit tests for the prompt builder and the wizard rules are in `app/src/test`.

## Credits

Made by Faysal Aziz ([faysalaziz.com](https://faysalaziz.com)) with Claude Code.
