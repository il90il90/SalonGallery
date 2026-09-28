# Privacy Policy — Salon Gallery

_Last updated: 28 September 2026_

Salon Gallery ("the app") is a digital photo-frame app for Android. This policy explains what the
app does and does not do with your information.

## Summary

**Salon Gallery does not collect, transmit, sell, or share any personal data.** There are no
accounts, no analytics, no advertising, and no third-party tracking. Your photos, videos and music
stay on your own devices and your own local Wi-Fi network.

## What the app stores

- **Your media** (photos, videos, audio) that you choose to send is stored **only on the Display
  device you send it to**, in that app's private storage. You can delete it at any time from the
  Remote ("Clear all", or delete individual items).
- **Your settings** (chosen role, selected frame, slideshow options, optional screen name, optional
  screen PIN, sleep schedule) are stored locally on the device using Android DataStore and
  SharedPreferences. The screen PIN is stored on the Display device only and never leaves it.

The app has no server of its own and no cloud account. Nothing above is uploaded anywhere.

## Network access

- **Local network:** The Remote and Display communicate directly over your local Wi-Fi using
  service discovery (NSD) and plain HTTP on the LAN. This traffic never leaves your network.
- **Internet (optional, only when you ask):** If you use the built-in **Art gallery** or **Free
  music library**, the Display device downloads the public-domain artwork or track you tap from its
  public source (the Art Institute of Chicago open API and the Internet Archive). Only the item you
  select is requested. No personal information is sent with these requests.
- **Updates:** The app checks the project's public GitHub Releases feed for a newer version and can
  download and install an update if you choose. This is an anonymous request to GitHub.

## Permissions and why they are used

- **Internet / Network state / Wi-Fi state / Multicast** — to discover and talk to the other device
  on your local network, and to download optional art/music and updates.
- **Foreground service (media playback)** — to keep the Display showing your gallery and playing
  music while minimised.
- **Post notifications** — to show the "Displaying your gallery" foreground-service notification.
- **Request install packages** — so the app can install its own self-update that you download.
- **Wake lock** — to keep the Display awake as a frame.

The app does **not** request access to your contacts, location, microphone, or camera.

## Children

The app is not directed at children and collects no data from anyone.

## Changes

If this policy changes, the updated version will be published in this repository.

## Contact

Questions: israel@m-eylon.com
