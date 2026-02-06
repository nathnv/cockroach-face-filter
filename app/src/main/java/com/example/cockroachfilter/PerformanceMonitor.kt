package com.example.cockroachfilter

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import kotlin.math.roundToInt

/**
 * Performance monitoring and optimization
 */
class PerformanceMonitor(private val context: Context) {
    companion object {
        private const val TAG = "PerformanceMonitor"
    }

    private var frameCount = 0
    private var lastFrameTime = System.currentTimeMillis()
    private var fps = 0
    private val frameTimings = mutableListOf<Long>()
    private val maxFrameTimings = 60

    /**
     * Update frame metrics
     */
    fun updateFrame() {
        frameCount++
        val currentTime = System.currentTimeMillis()
        val frameTime = currentTime - lastFrameTime

        frameTimings.add(frameTime)
        if (frameTimings.size > maxFrameTimings) {
            frameTimings.removeAt(0)
        }

        if (currentTime - lastFrameTime >= 1000) {
            fps = frameCount
            frameCount = 0
            lastFrameTime = currentTime
        }
    }

    /**
     * Get current FPS
     */
    fun getFPS(): Int = fps

    /**
     * Get average frame time in milliseconds
     */
    fun getAverageFrameTime(): Float {
        return if (frameTimings.isEmpty()) 0f else frameTimings.average().toFloat()
    }

    /**
     * Get memory usage in MB
     */
    fun getMemoryUsage(): Float {
        return try {
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
            usedMemory.toFloat()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get memory usage", e)
            0f
        }
    }

    /**
     * Get native heap memory in MB
     */
    fun getNativeHeapMemory(): Float {
        return try {
            // Use runtime memory as approximation
            val runtime = Runtime.getRuntime()
            val nativeMemory = runtime.totalMemory() - runtime.freeMemory()
            (nativeMemory / 1048576L).toFloat()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get native heap", e)
            0f
        }
    }

    /**
     * Get available memory in MB
     */
    fun getAvailableMemory(): Float {
        return try {
            val runtime = Runtime.getRuntime()
            (runtime.maxMemory() / 1048576L).toFloat()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get available memory", e)
            0f
        }
    }

    /**
     * Get CPU load percentage
     */
    fun getCPULoad(): Float {
        return try {
            val runtime = Runtime.getRuntime()
            val processors = runtime.availableProcessors()
            (processors * 25).toFloat() // Simplified CPU load
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get CPU load", e)
            0f
        }
    }

    /**
     * Determine if performance is good
     */
    fun isPerformanceGood(): Boolean {
        return fps >= 30 && getMemoryUsage() < 200
    }

    /**
     * Get performance summary
     */
    fun getPerformanceSummary(): PerformanceSummary {
        return PerformanceSummary(
            fps = fps,
            averageFrameTime = getAverageFrameTime().roundToInt(),
            memoryUsage = getMemoryUsage().roundToInt(),
            cpuLoad = getCPULoad().roundToInt(),
            isOptimal = isPerformanceGood()
        )
    }

    data class PerformanceSummary(
        val fps: Int,
        val averageFrameTime: Int,
        val memoryUsage: Int,
        val cpuLoad: Int,
        val isOptimal: Boolean
    )
}

/**
 * Adaptive quality manager based on performance
 */
class AdaptiveQualityManager(private val performanceMonitor: PerformanceMonitor) {
    enum class QualityLevel {
        LOW, MEDIUM, HIGH
    }

    private var currentQuality = QualityLevel.HIGH
    private var particleCount = 100
    private var shadowQuality = 1f
    private var effectsEnabled = true

    /**
     * Update quality based on performance
     */
    fun updateQuality() {
        val fps = performanceMonitor.getFPS()
        val memoryUsage = performanceMonitor.getMemoryUsage()

        currentQuality = when {
            fps < 20 || memoryUsage > 250 -> QualityLevel.LOW
            fps < 30 || memoryUsage > 200 -> QualityLevel.MEDIUM
            else -> QualityLevel.HIGH
        }

        when (currentQuality) {
            QualityLevel.LOW -> {
                particleCount = 20
                shadowQuality = 0.5f
                effectsEnabled = false
            }
            QualityLevel.MEDIUM -> {
                particleCount = 50
                shadowQuality = 0.75f
                effectsEnabled = true
            }
            QualityLevel.HIGH -> {
                particleCount = 100
                shadowQuality = 1f
                effectsEnabled = true
            }
        }
    }

    fun getCurrentQuality(): QualityLevel = currentQuality
    fun getParticleCount(): Int = particleCount
    fun getShadowQuality(): Float = shadowQuality
    fun areEffectsEnabled(): Boolean = effectsEnabled
}
