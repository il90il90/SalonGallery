# Salon Gallery

Turn any Android device — a TV or a wall-mounted tablet — into a framed digital-art display.
A phone acts as a **Remote** that sends photos, videos and music over your **local Wi-Fi** to the
**Display Screen**, which shows them singly or as a slideshow inside realistic frames, with an
optional clock and text overlay.

Everything happens on your own network. No account, no cloud, no data leaves your home.

## Features

- **Local-network pairing** — the Display advertises itself over NSD; the Remote finds it automatically. No pairing codes.
- **Photo gallery** — send hundreds of photos at once; order them, shuffle, or show one now. Albums & categories.
- **Smart fit** — Fill / Fit / Blur-background so any aspect ratio looks right, plus a per-photo **Studio** (pinch-crop & reposition, WYSIWYG to the screen).
- **Slideshow** — Fade / Slide / Zoom / Ken-Burns transitions, adjustable interval, portrait or landscape.
- **Realistic frames** — 11 gallery moldings (Gold, Walnut, Noir, Rose Gold, …).
- **Video** — fills the screen edge-to-edge (ExoPlayer).
- **Music** — build a looping playlist from your phone, or download royalty-free public-domain tracks straight to the screen. Play / pause / next / shuffle.
- **Art gallery** — browse thousands of **public-domain masterpieces** (Art Institute of Chicago) and send any to your screen over Wi-Fi.
- **Text & clock overlay** — a welcome message and a live clock/date.
- **Always-on & background** — keeps the screen awake and runs as a foreground service so it survives minimising.
- **Screen security** — name each screen, protect its settings with a PIN, and set an **auto-sleep schedule** (dims to a quiet clock during quiet hours).
- **Self-update** — checks GitHub Releases and installs updates in-app.

## Download

Grab the latest APK from the [**Releases**](https://github.com/il90il90/SalonGallery/releases/latest) page.

1. Download `SalonGallery-<version>.apk` to your Android device.
2. Open it and allow installing from unknown sources when prompted.
3. On first launch, pick whether the device is a **Display Screen** or a **Remote**.
4. Put both devices on the same Wi-Fi. The Remote finds the screen automatically.

The app checks Releases on launch and can update itself in-app.

## How it works

The Display runs a small embedded HTTP server (NanoHTTPD) and advertises it via Network Service
Discovery (`_salongallery._tcp`). The Remote discovers it with `NsdManager` and sends media and
commands over plain HTTP on the LAN. Nothing is sent to the internet except the optional art /
music downloads, which the **Display** fetches directly from public-domain sources.

## Design

"Electric Dark" — a deep-space background (#0B0B14) with neon cyan → blue → violet accents,
Space Grotesk + Inter type, built entirely with Jetpack Compose (Material 3). English, LTR.

## Build from source

Requires the Android SDK and JDK 17.

```bash
./gradlew :app:assembleDebug      # debug APK
./gradlew :app:assembleRelease    # signed release APK (needs keystore.properties)
./gradlew :app:bundleRelease      # Play Store App Bundle (.aab)
```

Release signing reads `keystore.properties` (`storeFile`, `storePassword`, `keyAlias`, `keyPassword`),
all git-ignored. `local.properties` may optionally define `PEXELS_API_KEY` (unused by default — the
art gallery uses the key-free Art Institute of Chicago API).

## Privacy

Salon Gallery collects **no personal data** and has no analytics or ads. See [PRIVACY.md](PRIVACY.md).

## Tech

Kotlin · Jetpack Compose · Material 3 · Media3 ExoPlayer · Coil · NanoHTTPD · NSD · DataStore
· min SDK 26 · target SDK 35.
