package com.il90.salongallery.upload

import android.content.ContentResolver
import android.net.Uri

/** How photos are uploaded: downscaled for the wall, or exactly as taken. (Videos are never altered.) */
enum class UploadQuality { OPTIMIZED, ORIGINAL }

/** Longest edge, in px, an "Optimized" photo is scaled down to — well above the wall's 1600px decode so
 *  it still looks crisp and allows some zoom, while being a fraction of a modern phone photo's size. */
private const val OPTIMIZE_MAX_EDGE = 2560

/**
 * For the "Optimized" upload choice: downscale a photo to a wall-friendly size and re-encode it, so it
 * transfers fast, stores small and never forces a huge decode on the screen. The EXIF orientation is
 * baked into the pixels (so the re-encoded JPEG still shows upright even though EXIF is dropped). Any
 * failure — or a photo already small enough and needing no rotation — returns the original bytes.
 */
fun optimizePhotoForUpload(bytes: ByteArray): ByteArray = runCatching {
    val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
    android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
    val longEdge = maxOf(bounds.outWidth, bounds.outHeight)
    if (longEdge <= 0) return@runCatching bytes
    val exifRot = runCatching {
        when (android.media.ExifInterface(java.io.ByteArrayInputStream(bytes))
            .getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL)) {
            android.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            android.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            android.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }.getOrDefault(0f)
    // Already small and upright → leave it exactly as it is.
    if (longEdge <= OPTIMIZE_MAX_EDGE && exifRot == 0f && bytes.size <= 2_500_000) return@runCatching bytes
    var sample = 1
    while (longEdge / sample > OPTIMIZE_MAX_EDGE * 2) sample *= 2
    val decoded = android.graphics.BitmapFactory.decodeByteArray(
        bytes, 0, bytes.size, android.graphics.BitmapFactory.Options().apply { inSampleSize = sample },
    ) ?: return@runCatching bytes
    val scale = OPTIMIZE_MAX_EDGE.toFloat() / maxOf(decoded.width, decoded.height)
    val scaled = if (scale < 1f)
        android.graphics.Bitmap.createScaledBitmap(
            decoded, (decoded.width * scale).toInt().coerceAtLeast(1), (decoded.height * scale).toInt().coerceAtLeast(1), true,
        )
    else decoded
    val upright = if (exifRot != 0f)
        android.graphics.Bitmap.createBitmap(scaled, 0, 0, scaled.width, scaled.height, android.graphics.Matrix().apply { postRotate(exifRot) }, true)
    else scaled
    val bos = java.io.ByteArrayOutputStream()
    upright.compress(android.graphics.Bitmap.CompressFormat.JPEG, 88, bos)
    if (upright !== scaled) upright.recycle()
    if (scaled !== decoded) scaled.recycle()
    decoded.recycle()
    val out = bos.toByteArray()
    if (out.isNotEmpty() && out.size < bytes.size) out else bytes
}.getOrDefault(bytes)

/** Exact byte length of a content Uri, or -1 if unknown (then the caller buffers instead of streaming). */
fun uriLength(cr: ContentResolver, uri: Uri): Long = runCatching {
    cr.openAssetFileDescriptor(uri, "r")?.use { val l = it.length; if (l >= 0) l else -1L } ?: -1L
}.getOrElse { -1L }
