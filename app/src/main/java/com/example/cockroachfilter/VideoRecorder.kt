package com.example.cockroachfilter

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Video recording manager for AR filter output
 */
class VideoRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var currentOutputFile: File? = null

    companion object {
        private const val TAG = "VideoRecorder"
        private const val VIDEO_BITRATE = 5000000 // 5 Mbps
        private const val AUDIO_BITRATE = 128000 // 128 kbps
        private const val VIDEO_FPS = 30
    }

    /**
     * Start recording video
     */
    fun startRecording(outputFile: File): Boolean {
        return try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setVideoEncodingBitRate(VIDEO_BITRATE)
                setAudioEncodingBitRate(AUDIO_BITRATE)
                setVideoFrameRate(VIDEO_FPS)
                setVideoSize(1080, 1920) // Portrait orientation
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            currentOutputFile = outputFile
            isRecording = true
            Log.d(TAG, "Recording started: ${outputFile.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            false
        }
    }

    /**
     * Stop recording and save file
     */
    fun stopRecording(): File? {
        return try {
            if (isRecording) {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
                isRecording = false
                Log.d(TAG, "Recording stopped")
                currentOutputFile
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording", e)
            null
        }
    }

    /**
     * Create output video file
     */
    fun createVideoFile(): File {
        val storageDir = context.getExternalFilesDir("videos")
        storageDir?.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        return File(storageDir, "cockroach_$timeStamp.mp4")
    }

    /**
     * Check if currently recording
     */
    fun isCurrentlyRecording(): Boolean = isRecording

    /**
     * Release resources
     */
    fun release() {
        if (isRecording) {
            stopRecording()
        }
        mediaRecorder?.release()
        mediaRecorder = null
    }
}

/**
 * Screenshot manager for saving images to gallery
 */
class ScreenshotManager(private val context: Context) {
    companion object {
        private const val TAG = "ScreenshotManager"
    }

    /**
     * Save bitmap to gallery
     */
    fun saveToGallery(bitmap: android.graphics.Bitmap): Boolean {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
            val filename = "cockroach_$timeStamp.jpg"

            val storageDir = context.getExternalFilesDir("pictures")
            storageDir?.mkdirs()
            val file = File(storageDir, filename)

            file.outputStream().use { output ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, output)
            }

            Log.d(TAG, "Screenshot saved: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save screenshot", e)
            false
        }
    }

    /**
     * Create thumbnail from bitmap
     */
    fun createThumbnail(bitmap: android.graphics.Bitmap, width: Int = 100, height: Int = 100): android.graphics.Bitmap {
        return android.graphics.Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
