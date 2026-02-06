package com.example.cockroachfilter

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Main Activity for Cockroach Face Filter AR App
 * Handles camera, face detection, AR rendering, and recording
 */
class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var arSurfaceView: ARSurfaceView
    private var faceDetector: FaceDetector? = null
    private var cockroachAnimator: CockroachAnimator? = null
    private var particleSystem: ParticleSystem? = null
    private var reactionEffects: ReactionEffects? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var isFilterEnabled = true

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val TAG = "CockroachFilter"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        arSurfaceView = findViewById(R.id.arSurfaceView)

        // Initialize face detector
        faceDetector = FaceDetector(this)
        particleSystem = ParticleSystem(100)
        reactionEffects = ReactionEffects()

        // Check permissions
        if (hasAllPermissions()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Setup UI controls
        setupControls()
    }

    private fun hasAllPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startCamera()
        } else {
            Toast.makeText(this, "Permissions required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Image analysis for face detection
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                            processFaceDetection(imageProxy)
                        }
                    }

                // Select front camera
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                // Bind to lifecycle
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)

                Log.d(TAG, "Camera started successfully")
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processFaceDetection(imageProxy: ImageProxy) {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                // Convert ImageProxy to Bitmap
                val bitmap = imageProxy.toBitmap()

                // Detect face landmarks
                val result = faceDetector?.detectFaceLandmarks(bitmap)

                if (result?.faceDetected == true) {
                    // Initialize animator if needed
                    if (cockroachAnimator == null) {
                        val faceBounds = faceDetector?.getFaceBounds(result.landmarks)
                        faceBounds?.let {
                            cockroachAnimator = CockroachAnimator(
                                it.x + it.width / 2,
                                it.y + it.height / 2
                            )
                        }
                    }

                    // Update cockroach animation
                    cockroachAnimator?.let { animator ->
                        val faceBounds = faceDetector?.getFaceBounds(result.landmarks)
                        faceBounds?.let { bounds ->
                            val facialExpressions = faceDetector?.calculateFacialExpressions(result.landmarks)
                            facialExpressions?.let {
                                val state = animator.update(
                                    bounds.width,
                                    bounds.height,
                                    bounds.x,
                                    bounds.y,
                                    it
                                )

                                // Update AR surface view
                                arSurfaceView.cockroachState = state
                                arSurfaceView.particleSystem = particleSystem
                                arSurfaceView.reactionEffects = reactionEffects

                                // Create particles
                                if (it.eyesClosed > 0.5f || it.mouthOpen > 0.5f) {
                                    particleSystem?.createFearParticles(state.x, state.y, 2)
                                } else {
                                    particleSystem?.createDustParticles(state.x, state.y, 1)
                                }

                                // Update reactions
                                reactionEffects?.updateReactions(
                                    it.eyesClosed,
                                    it.mouthOpen,
                                    it.headTurn
                                )
                            }
                        }
                    }

                    // Update particles
                    particleSystem?.update()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Face detection error", e)
            } finally {
                imageProxy.close()
            }
        }
    }

    private fun setupControls() {
        findViewById<View>(R.id.toggleFilterButton).setOnClickListener {
            isFilterEnabled = !isFilterEnabled
            arSurfaceView.visibility = if (isFilterEnabled) View.VISIBLE else View.GONE
            (it as android.widget.Button).text = if (isFilterEnabled) "Filter: ON" else "Filter: OFF"
        }

        findViewById<View>(R.id.recordButton).setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        findViewById<View>(R.id.screenshotButton).setOnClickListener {
            takeScreenshot()
        }

        findViewById<View>(R.id.statsButton).setOnClickListener {
            arSurfaceView.showStats = !arSurfaceView.showStats
        }
    }

    private fun startRecording() {
        try {
            val outputFile = createVideoFile()
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Recording error", e)
            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            Toast.makeText(this, "Recording saved to gallery", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Stop recording error", e)
        }
    }

    private fun takeScreenshot() {
        try {
            val bitmap = arSurfaceView.getDrawingCache()
            if (bitmap != null) {
                saveImageToGallery(bitmap)
                Toast.makeText(this, "Screenshot saved to gallery", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Screenshot error", e)
            Toast.makeText(this, "Failed to save screenshot", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createVideoFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        return File.createTempFile("cockroach_$timeStamp", ".mp4", storageDir)
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "cockroach_$timeStamp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        faceDetector?.release()
        mediaRecorder?.release()
    }
}

/**
 * Extension function to convert ImageProxy to Bitmap
 */
private fun ImageProxy.toBitmap(): Bitmap {
    val planes = this.planes
    val buffer = planes[0].buffer
    buffer.rewind()
    val w = this.width
    val h = this.height
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}
