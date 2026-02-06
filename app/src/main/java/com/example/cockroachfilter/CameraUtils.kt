package com.example.cockroachfilter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

/**
 * Camera utilities for image processing and conversion
 */
object CameraUtils {
    private const val TAG = "CameraUtils"

    /**
     * Convert ImageProxy to Bitmap
     */
    fun imageToBitmap(image: ImageProxy): Bitmap {
        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        buffer.rewind()
        val pixelStride = planes[0].pixelStride
        val w = image.width
        val h = image.height
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }

    /**
     * Rotate bitmap to correct orientation
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f) return bitmap

        val matrix = Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Mirror bitmap for front camera
     */
    fun mirrorBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Resize bitmap to target dimensions
     */
    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    /**
     * Crop bitmap to face region
     */
    fun cropToFace(bitmap: Bitmap, faceBounds: FaceBounds): Bitmap {
        val x = faceBounds.x.toInt().coerceAtLeast(0)
        val y = faceBounds.y.toInt().coerceAtLeast(0)
        val width = faceBounds.width.toInt().coerceAtMost(bitmap.width - x)
        val height = faceBounds.height.toInt().coerceAtMost(bitmap.height - y)

        return if (width > 0 && height > 0) {
            Bitmap.createBitmap(bitmap, x, y, width, height)
        } else {
            bitmap
        }
    }

    /**
     * Get camera rotation degrees for orientation
     */
    fun getCameraRotationDegrees(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
        return when (windowManager.defaultDisplay.rotation) {
            android.view.Surface.ROTATION_0 -> 0
            android.view.Surface.ROTATION_90 -> 90
            android.view.Surface.ROTATION_180 -> 180
            android.view.Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    /**
     * Calculate aspect ratio
     */
    fun calculateAspectRatio(width: Int, height: Int): Float {
        return width.toFloat() / height.toFloat()
    }

    /**
     * Check if bitmap is valid
     */
    fun isValidBitmap(bitmap: Bitmap?): Boolean {
        return bitmap != null && !bitmap.isRecycled && bitmap.width > 0 && bitmap.height > 0
    }

    /**
     * Get bitmap file size in bytes
     */
    fun getBitmapSize(bitmap: Bitmap): Long {
        return (bitmap.width * bitmap.height * 4).toLong()
    }
}

/**
 * Image processing pipeline for face detection
 */
class ImageProcessingPipeline {
    private var lastProcessedTime = 0L
    private val processingInterval = 33L // ~30 FPS

    /**
     * Process image with throttling to maintain target FPS
     */
    fun processImage(
        image: ImageProxy,
        processor: (Bitmap) -> Unit
    ): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessedTime < processingInterval) {
            return false
        }

        try {
            val bitmap = CameraUtils.imageToBitmap(image)
            val rotatedBitmap = CameraUtils.rotateBitmap(bitmap, 90f)
            val mirroredBitmap = CameraUtils.mirrorBitmap(rotatedBitmap)

            processor(mirroredBitmap)

            bitmap.recycle()
            rotatedBitmap.recycle()
            mirroredBitmap.recycle()

            lastProcessedTime = currentTime
            return true
        } catch (e: Exception) {
            Log.e("ImageProcessingPipeline", "Error processing image", e)
            return false
        }
    }

    /**
     * Reset throttle timer
     */
    fun reset() {
        lastProcessedTime = 0L
    }
}

/**
 * Camera frame buffer manager
 */
class FrameBuffer(private val maxFrames: Int = 10) {
    private val frames = mutableListOf<Bitmap>()

    /**
     * Add frame to buffer
     */
    fun addFrame(bitmap: Bitmap) {
        frames.add(bitmap)
        if (frames.size > maxFrames) {
            frames.removeAt(0).recycle()
        }
    }

    /**
     * Get latest frame
     */
    fun getLatestFrame(): Bitmap? = frames.lastOrNull()

    /**
     * Get frame at index
     */
    fun getFrame(index: Int): Bitmap? = frames.getOrNull(index)

    /**
     * Get frame count
     */
    fun getFrameCount(): Int = frames.size

    /**
     * Clear all frames
     */
    fun clear() {
        frames.forEach { it.recycle() }
        frames.clear()
    }
}
