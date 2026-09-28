# Google Play — Store listing & release prep

Copy-ready metadata for publishing Salon Gallery on Google Play.

## App details

- **App name:** Salon Gallery
- **Package:** `com.meylon.salongallery`
- **Category:** Personalization (alt: Art & Design)
- **Content rating:** Everyone
- **Contact email:** israel@m-eylon.com
- **Privacy policy URL:** https://github.com/il90il90/SalonGallery/blob/main/PRIVACY.md

## Short description (≤ 80 chars)

> Turn any TV or tablet into a framed digital-art display, controlled from your phone.

## Full description

> **Salon Gallery turns any Android TV or tablet into a beautiful framed digital-art display — and your phone becomes the remote.**
>
> Put one device on the wall as your **Display**, open the app as a **Remote** on your phone, and they find each other automatically over your Wi-Fi. Everything stays on your own network.
>
> **Show your photos, beautifully**
> • Send hundreds of photos at once, arrange the order or shuffle
> • Organise them into albums
> • Smart fit (Fill / Fit / soft Blur background) so every photo looks right
> • A per-photo Studio to pinch-crop and reposition exactly how it appears on the wall
> • Elegant transitions: Fade, Slide, Zoom, Ken Burns
> • 11 realistic frames — Gold, Walnut, Noir, Rose Gold and more
>
> **More than photos**
> • Play videos edge-to-edge
> • Build a background-music playlist from your phone, or download royalty-free public-domain tracks straight to the screen
> • Browse thousands of public-domain masterpieces from the Art Institute of Chicago and send them to your wall
> • Add a welcome message and a live clock & date
>
> **Made to live on your wall**
> • Stays awake and keeps running in the background
> • Name each screen and protect its settings with a PIN
> • Auto-sleep schedule dims the frame to a quiet clock overnight and wakes it in the morning
>
> **Private by design** — no account, no cloud, no ads, no tracking. Your photos never leave your home network. See our privacy policy.

## Data safety form

- Data collected: **None**
- Data shared: **None**
- No data is collected or transmitted. Optional in-app downloads (art/music/updates) are anonymous requests initiated by the user; no personal data is attached.

## Graphics needed (to produce before submitting)

- App icon 512×512 (from the adaptive `ic_launcher`).
- Feature graphic 1024×500.
- Phone screenshots (Remote): control panel, library manager, Studio, Music sheet, Art gallery.
- Tablet/TV screenshots (Display): a framed slideshow, the clock overlay.

## Release build

```bash
./gradlew :app:bundleRelease   # -> app/build/outputs/bundle/release/app-release.aab
```

Upload the `.aab`. Recommended: enrol in **Play App Signing** (Play manages the signing key; the
upload key stays `salongallery-release.jks`).

## After moving to Play

The in-app self-updater (GitHub Releases) is for sideloaded builds. Once on Play, switch updates to
the **Play In-App Updates** API and let Play handle distribution; the GitHub updater can stay as a
fallback for sideloaded copies.
