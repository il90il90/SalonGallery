# R8 is enabled for release. Most libraries (Compose, Media3, Coil, OkHttp,
# coroutines, DataStore) ship their own consumer rules, so keep this minimal.

# NanoHTTPD is a plain jar without consumer rules — keep it whole to be safe.
-keep class fi.iki.elonen.** { *; }
-dontwarn fi.iki.elonen.**

# Keep enum valueOf/values (used across the app for effects, frames, fit, etc.).
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
