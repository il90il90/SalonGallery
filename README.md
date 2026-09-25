# Salon Gallery

Turn any Android device — a TV or a wall-mounted tablet — into a framed digital-art display.
A phone acts as a **remote** that sends photos over your local network to the **display screen**,
which shows them singly or as a slideshow inside selectable frames.

> Status: early development. This first build ships the onboarding (role selection),
> settings scaffolding and a built-in self-updater. Network pairing, frames, slideshow,
> the Pexels wallpaper gallery and the smart collage are on the way.

## Download

Grab the latest APK from the [**Releases**](https://github.com/il90il90/SalonGallery/releases/latest) page.

1. Download `app-release.apk` to your Android device.
2. Open it and allow installing from unknown sources when prompted.
3. On first launch, pick whether the device is a **Display Screen** or a **Remote**.

The app checks Releases for newer versions and can update itself in-app.

## Design

"Electric Dark" — deep-space background with neon cyan → blue → violet accents,
built with Jetpack Compose (Material 3). English, LTR.

## Build from source

Requires the Android SDK and JDK 17.

```bash
./gradlew :app:assembleDebug      # debug APK
./gradlew :app:assembleRelease    # signed release APK (needs keystore.properties)
```

`local.properties` may define `PEXELS_API_KEY` for the wallpaper gallery (later phase).
Release signing reads `keystore.properties` (`storeFile`, `storePassword`, `keyAlias`, `keyPassword`).

## Tech

Kotlin · Jetpack Compose · Material 3 · DataStore · min SDK 26 · target SDK 35.
