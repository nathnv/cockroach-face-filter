package com.example.cockroachfilter

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Face landmark detection using TensorFlow Lite
 * Detects 468 facial landmarks for AR rendering
 */
class FaceDetector(private val context: Context) {
    private var interpreter: Interpreter? = null
    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    data class FaceLandmark(
        val x: Float,
        val y: Float,
        val z: Float,
        val confidence: Float
    )

    data class FaceDetectionResult(
        val landmarks: List<FaceLandmark>,
        val faceDetected: Boolean,
        val confidence: Float
    )

    init {
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, "face_landmarker.tflite")
            interpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Detect face landmarks from bitmap
     */
    fun detectFaceLandmarks(bitmap: Bitmap): FaceDetectionResult {
        if (interpreter == null) {
            return FaceDetectionResult(emptyList(), false, 0f)
        }

        try {
            // Prepare input
            val tensorImage = TensorImage()
            tensorImage.load(bitmap)
            val processedImage = imageProcessor.process(tensorImage)

            // Run inference
            val inputBuffer = processedImage.tensorBuffer.buffer
            val outputBuffer = ByteBuffer.allocateDirect(468 * 3 * 4)
                .order(ByteOrder.nativeOrder())

            val inputs = arrayOf<Any>(inputBuffer)
            val outputs = mapOf(0 to outputBuffer)

            interpreter!!.runForMultipleInputsOutputs(inputs, outputs)

            // Parse output
            outputBuffer.rewind()
            val landmarks = mutableListOf<FaceLandmark>()

            for (i in 0 until 468) {
                val x = outputBuffer.float * bitmap.width
                val y = outputBuffer.float * bitmap.height
                val z = outputBuffer.float
                val confidence = outputBuffer.float

                landmarks.add(FaceLandmark(x, y, z, confidence))
            }

            val avgConfidence = landmarks.map { it.confidence }.average().toFloat()
            val faceDetected = avgConfidence > 0.5f

            return FaceDetectionResult(landmarks, faceDetected, avgConfidence)
        } catch (e: Exception) {
            e.printStackTrace()
            return FaceDetectionResult(emptyList(), false, 0f)
        }
    }

    /**
     * Calculate facial expressions from landmarks
     */
    fun calculateFacialExpressions(landmarks: List<FaceLandmark>): FacialExpressions {
        if (landmarks.size < 468) {
            return FacialExpressions(0f, 0f, 0f)
        }

        // Mouth open: distance between upper and lower lip
        val upperLip = landmarks[13]
        val lowerLip = landmarks[14]
        val mouthOpen = kotlin.math.max(0f, kotlin.math.min(1f, kotlin.math.abs(lowerLip.y - upperLip.y) * 3))

        // Eyes closed: check eye openness
        val leftEyeTop = landmarks[159]
        val leftEyeBottom = landmarks[145]
        val rightEyeTop = landmarks[386]
        val rightEyeBottom = landmarks[374]
        val leftEyeOpenness = kotlin.math.abs(leftEyeBottom.y - leftEyeTop.y)
        val rightEyeOpenness = kotlin.math.abs(rightEyeBottom.y - rightEyeTop.y)
        val eyesClosed = kotlin.math.max(0f, 1 - (leftEyeOpenness + rightEyeOpenness) / 0.1f)

        // Head turn: nose position relative to eyes
        val leftEye = landmarks[33]
        val rightEye = landmarks[263]
        val noseTip = landmarks[1]
        val headTurn = (noseTip.x - (leftEye.x + rightEye.x) / 2) * 2

        return FacialExpressions(
            mouthOpen = kotlin.math.min(1f, mouthOpen),
            eyesClosed = kotlin.math.min(1f, kotlin.math.max(0f, eyesClosed)),
            headTurn = headTurn
        )
    }

    /**
     * Get face bounds from landmarks
     */
    fun getFaceBounds(landmarks: List<FaceLandmark>): FaceBounds {
        if (landmarks.isEmpty()) {
            return FaceBounds(0f, 0f, 0f, 0f)
        }

        val minX = landmarks.minOf { it.x }
        val maxX = landmarks.maxOf { it.x }
        val minY = landmarks.minOf { it.y }
        val maxY = landmarks.maxOf { it.y }

        return FaceBounds(minX, minY, maxX - minX, maxY - minY)
    }

    fun release() {
        interpreter?.close()
        interpreter = null
    }
}

data class FacialExpressions(
    val mouthOpen: Float,
    val eyesClosed: Float,
    val headTurn: Float
)

data class FaceBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)
