package com.il90.salongallery.net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.FaceDetector
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Guesses how a photo should be turned so the people in it stand upright.
 *
 * Primary detector is **ML Kit face detection** (on-device, bundled model — no Play Services, works
 * on a plain Android TV box): far better than the old platform detector at finding faces, including
 * children, profiles and small faces. We physically try the four quarter-turns and feed each as an
 * upright image; ML Kit only locks onto a roughly-upright face, so the turn that yields the most /
 * largest faces is "up". If ML Kit is ever unavailable it falls back to the platform
 * [android.media.FaceDetector]. Returns the extra display rotation (0/90/180/270 clockwise, matching
 * Compose `rotationZ`), or 0 when there is no face or the photo is already upright.
 */
object FaceOrient {
    private const val SIZE = 1280     // detection resolution — big enough that a small child's face in a 4000px photo is still found
    private const val MAX_FACES = 4
    private const val MIN_CONFIDENCE = 0.4f
    /** A turn must beat the current best by this ratio before we rotate — avoids flip-flopping on ties. */
    private const val WIN_RATIO = 1.25f

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
            var best = scoreFaces(base)
            for (rot in intArrayOf(90, 180, 270)) {
                val turned = Bitmap.createBitmap(base, 0, 0, base.width, base.height, Matrix().apply { postRotate(rot.toFloat()) }, true)
                val f = scoreFaces(turned)
                if (turned !== base) turned.recycle()
                // Clearly better only (a relative margin, since ML-area and legacy-confidence scores
                // live on different scales): a tie or a marginal win keeps the photo as it is.
                if (f.score > best.score * WIN_RATIO + 1e-4f) { best = f; bestRot = rot }
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

    // ML Kit detector, created once and reused; FAST mode, with eye/mouth landmarks for the centre.
    @Volatile private var mlAvailable = true
    private val mlDetector by lazy {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                // ACCURATE + a small min face size: these are family photos where the face we need to
                // find is often a single small child far from the camera — recall matters more than speed
                // (detection runs on a low-priority background thread, so it never janks the app).
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setMinFaceSize(0.04f)
                .build()
        )
    }

    /**
     * ML Kit first (far better at finding faces); the platform detector as a fallback. ML Kit only
     * locks onto a roughly-upright face, so feeding [src] as an upright image and measuring the
     * faces found tells us whether [src] is the right way up. A per-image failure quietly falls back
     * to the platform detector for that one photo; only if the detector can't be built at all do we
     * stop trying ML Kit for the session.
     */
    private fun scoreFaces(src: Bitmap): Faces {
        if (mlAvailable) {
            val detector = runCatching { mlDetector }.getOrElse { mlAvailable = false; null }
            if (detector != null) {
                val ml = runCatching {
                    val faces = Tasks.await(detector.process(InputImage.fromBitmap(src, 0)), 5, TimeUnit.SECONDS)
                    val area = (src.width * src.height).toFloat().coerceAtLeast(1f)
                    var total = 0f; var sx = 0f; var sy = 0f
                    for (f in faces) {
                        val b = f.boundingBox
                        val w = b.width().toFloat(); val h = b.height().toFloat()
                        if (w <= 0f || h <= 0f) continue
                        // Only count a face that is genuinely upright in THIS orientation: eyes above the
                        // mouth. This is what tells "the right way up" from a merely-detected face, so a
                        // correct photo of an upside-down child doesn't get flipped.
                        val le = f.getLandmark(com.google.mlkit.vision.face.FaceLandmark.LEFT_EYE)?.position
                        val re = f.getLandmark(com.google.mlkit.vision.face.FaceLandmark.RIGHT_EYE)?.position
                        val mo = f.getLandmark(com.google.mlkit.vision.face.FaceLandmark.MOUTH_BOTTOM)?.position
                        if (le != null && re != null && mo != null && (le.y + re.y) / 2f >= mo.y) continue
                        val weight = (w * h) / area               // bigger faces count more
                        total += weight
                        sx += b.exactCenterX() * weight
                        sy += b.exactCenterY() * weight
                    }
                    if (total > 0f) Faces(total, sx / total, sy / total) else Faces(0f, 0f, 0f)
                }.getOrNull()
                if (ml != null) return ml
            }
        }
        return faces(src)
    }

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
