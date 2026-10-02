package com.il90.salongallery.net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.FaceDetector
import java.io.File

/**
 * Guesses how a photo should be turned so the people in it stand upright.
 *
 * Uses the platform's built-in [android.media.FaceDetector] — no extra dependency, no APK weight —
 * which only recognises *upright* frontal faces. So we try the four quarter-turns and keep the one
 * where faces are found most confidently. Returns the extra display rotation to apply (0/90/180/270
 * clockwise, matching Compose `rotationZ`), or 0 when there is no face or the photo is fine as-is.
 */
object FaceOrient {
    private const val SIZE = 640      // detection resolution — small keeps each turn quick, big enough for kids in a group shot
    private const val MAX_FACES = 4
    private const val MIN_CONFIDENCE = 0.4f
    /** How much more face confidence a turn needs over the photo as-is before we rotate it. */
    private const val MARGIN = 0.1f

    /**
     * What we learnt about a photo: the extra [rot] to stand it upright, and — when faces were
     * found — where they are ([focus], normalised 0..1 in the photo as Coil draws it, i.e. EXIF
     * applied but BEFORE [rot]) plus that picture's [aspect] (width / height). The focus lets a
     * cropped photo keep the faces in view instead of the middle (often just a belly).
     */
    data class Result(val rot: Int, val focus: PhotoFocus?)

    fun detect(file: File): Int = analyze(file).rot

    fun analyze(file: File): Result = runCatching {
        val base = decodeAsShown(file) ?: return Result(0, null)
        try {
            var bestRot = 0
            var best = faces(base)
            for (rot in intArrayOf(90, 180, 270)) {
                val turned = Bitmap.createBitmap(base, 0, 0, base.width, base.height, Matrix().apply { postRotate(rot.toFloat()) }, true)
                val f = faces(turned)
                if (turned !== base) turned.recycle()
                // Clearly better only: a tie (or a marginal win) keeps the photo as it is.
                if (f.score > best.score + MARGIN) { best = f; bestRot = rot }
            }
            if (best.score <= 0f) return Result(0, null)
            // Map the upright faces' centre back into the un-turned picture's coordinates.
            val bw = base.width.toFloat(); val bh = base.height.toFloat()
            val (x, y) = when (bestRot) {
                90 -> best.cy to (bh - best.cx)
                180 -> (bw - best.cx) to (bh - best.cy)
                270 -> (bw - best.cy) to best.cx
                else -> best.cx to best.cy
            }
            Result(bestRot, PhotoFocus((x / bw).coerceIn(0f, 1f), (y / bh).coerceIn(0f, 1f), bw / bh))
        } finally {
            base.recycle()
        }
    }.getOrDefault(Result(0, null))

    /** Confidence-weighted centre of the faces found in [src] (its own pixel coordinates). */
    private class Faces(val score: Float, val cx: Float, val cy: Float)

    /** Sum of face confidences above the floor — 0 when nothing upright is found. */
    private fun faces(src: Bitmap): Faces {
        // FaceDetector wants RGB_565 with an even width.
        val w = src.width and 1.inv()
        val h = src.height
        if (w < 32 || h < 32) return Faces(0f, 0f, 0f)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        Canvas(bmp).drawBitmap(src, 0f, 0f, null)
        val found = arrayOfNulls<FaceDetector.Face>(MAX_FACES)
        val n = runCatching { FaceDetector(w, h, MAX_FACES).findFaces(bmp, found) }.getOrDefault(0)
        bmp.recycle()
        var total = 0f; var sx = 0f; var sy = 0f
        val mid = android.graphics.PointF()
        for (i in 0 until n) {
            val face = found[i] ?: continue
            val c = face.confidence()
            if (c < MIN_CONFIDENCE) continue
            face.getMidPoint(mid)
            total += c
            sx += mid.x * c
            // The midpoint is between the eyes; the face's centre sits a little lower.
            sy += (mid.y + face.eyesDistance() * 0.5f) * c
        }
        return if (total > 0f) Faces(total, sx / total, sy / total) else Faces(0f, 0f, 0f)
    }

    /**
     * Decode downscaled with the file's EXIF orientation applied, so we judge the picture the way
     * Coil will actually draw it — our stored rotation is then purely on top of that.
     */
    private fun decodeAsShown(file: File): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (bounds.outWidth / sample > SIZE || bounds.outHeight / sample > SIZE) sample *= 2
        val bmp = BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample }) ?: return null
        val exifRot = runCatching {
            when (ExifInterface(file.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        }.getOrDefault(0f)
        if (exifRot == 0f) return bmp
        val out = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, Matrix().apply { postRotate(exifRot) }, true)
        if (out !== bmp) bmp.recycle()
        return out
    }
}
