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
    private const val SIZE = 480      // detection resolution — small keeps each turn ~100 ms
    private const val MAX_FACES = 4
    private const val MIN_CONFIDENCE = 0.4f

    fun detect(file: File): Int = runCatching {
        val base = decodeAsShown(file) ?: return 0
        try {
            var bestRot = 0
            var bestScore = score(base)
            for (rot in intArrayOf(90, 180, 270)) {
                val turned = Bitmap.createBitmap(base, 0, 0, base.width, base.height, Matrix().apply { postRotate(rot.toFloat()) }, true)
                val s = score(turned)
                if (turned !== base) turned.recycle()
                // Strictly greater: a tie keeps the photo as it is.
                if (s > bestScore) { bestScore = s; bestRot = rot }
            }
            if (bestScore <= 0f) 0 else bestRot
        } finally {
            base.recycle()
        }
    }.getOrDefault(0)

    /** Sum of face confidences above the floor — 0 when nothing upright is found. */
    private fun score(src: Bitmap): Float {
        // FaceDetector wants RGB_565 with an even width.
        val w = src.width and 1.inv()
        val h = src.height
        if (w < 32 || h < 32) return 0f
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        Canvas(bmp).drawBitmap(src, 0f, 0f, null)
        val faces = arrayOfNulls<FaceDetector.Face>(MAX_FACES)
        val n = runCatching { FaceDetector(w, h, MAX_FACES).findFaces(bmp, faces) }.getOrDefault(0)
        bmp.recycle()
        var total = 0f
        for (i in 0 until n) faces[i]?.confidence()?.let { if (it >= MIN_CONFIDENCE) total += it }
        return total
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
