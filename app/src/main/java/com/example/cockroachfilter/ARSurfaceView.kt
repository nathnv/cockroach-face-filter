package com.example.cockroachfilter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom SurfaceView for rendering AR cockroach overlay
 */
class ARSurfaceView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {

    private var renderThread: RenderThread? = null
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    var cockroachState: CockroachAnimator.CockroachState? = null
    var particleSystem: ParticleSystem? = null
    var reactionEffects: ReactionEffects? = null
    var performanceMetrics: PerformanceMetrics? = null
    var showStats = false

    init {
        this.holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        renderThread = RenderThread(holder)
        renderThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        renderThread?.stopRendering()
        renderThread?.join()
    }

    private inner class RenderThread(private val holder: SurfaceHolder) : Thread() {
        private var isRunning = true
        private val fpsCounter = FPSCounter()

        fun stopRendering() {
            isRunning = false
        }

        override fun run() {
            while (isRunning) {
                val canvas = holder.lockCanvas() ?: continue

                try {
                    // Clear canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                    // Draw cockroach
                    cockroachState?.let { state ->
                        drawCockroach(canvas, state)
                    }

                    // Draw particles
                    particleSystem?.draw(canvas)

                    // Draw reaction indicators
                    cockroachState?.let { state ->
                        reactionEffects?.let { effects ->
                            drawReactionIndicators(canvas, state, effects)
                        }
                    }

                    // Draw performance stats
                    if (showStats) {
                        performanceMetrics?.let { metrics ->
                            drawPerformanceStats(canvas, metrics, fpsCounter)
                        }
                    }

                    fpsCounter.update()
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }

                // Target 60 FPS
                Thread.sleep(16)
            }
        }
    }

    private fun drawCockroach(canvas: Canvas, state: CockroachAnimator.CockroachState) {
        canvas.save()

        // Translate to cockroach position
        canvas.translate(state.x, state.y)
        canvas.rotate(Math.toDegrees(state.rotation.toDouble()).toFloat())
        canvas.scale(state.scale, state.scale)

        // Draw shadow
        paint.color = Color.argb(38, 0, 0, 0) // 15% black
        canvas.drawOval(-20f, 10f, 20f, 20f, paint)

        // Draw legs
        drawLegs(canvas, state)

        // Draw antennae
        drawAntennae(canvas, state)

        // Draw cockroach body (brown circle as fallback)
        paint.color = Color.rgb(139, 69, 19) // Brown
        canvas.drawCircle(0f, 0f, 20f, paint)

        // Draw shine
        paint.color = Color.argb(51, 255, 255, 255) // 20% white
        canvas.drawCircle(-5f, -5f, 8f, paint)

        // Add glow effect if running
        if (state.isRunning) {
            paint.color = Color.argb(153, 255, 100, 100) // Red glow
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            canvas.drawCircle(0f, 0f, 25f, paint)
            paint.style = Paint.Style.FILL

            // Motion lines
            for (i in 0..2) {
                paint.color = Color.argb((77 - i * 26).toInt(), 255, 100, 100)
                paint.strokeWidth = 1f
                canvas.drawLine(-20f - i * 5, 0f, -30f - i * 5, 0f, paint)
            }
        }

        canvas.restore()
    }

    private fun drawLegs(canvas: Canvas, state: CockroachAnimator.CockroachState) {
        val legs = calculateLegPositions(0f, 0f, 1f, 0f, state.legPhase)

        paint.color = Color.rgb(139, 69, 19) // Brown
        paint.strokeWidth = 2.5f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        val legPositions = listOf(
            Triple(-10f, -5f, -1),
            Triple(-10f, 0f, -1),
            Triple(-10f, 5f, -1),
            Triple(10f, -5f, 1),
            Triple(10f, 0f, 1),
            Triple(10f, 5f, 1)
        )

        legPositions.forEachIndexed { index, (posX, posY, side) ->
            val phase = (state.legPhase + (index % 2) * 0.5f) % 1f
            val legBend = sin(phase * PI.toFloat()) * 8

            val endX = posX + side * (15 + legBend)
            val endY = posY + legBend * 0.5f

            canvas.drawLine(posX, posY, endX, endY, paint)

            // Draw foot
            paint.style = Paint.Style.FILL
            paint.color = Color.rgb(101, 67, 33) // Dark brown
            canvas.drawCircle(endX, endY, 2.5f, paint)
            paint.style = Paint.Style.STROKE
            paint.color = Color.rgb(139, 69, 19)
        }
    }

    private fun drawAntennae(canvas: Canvas, state: CockroachAnimator.CockroachState) {
        paint.color = Color.rgb(139, 69, 19) // Brown
        paint.strokeWidth = 2f
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE

        val antennae = listOf(
            Pair(-5f, -12f),
            Pair(5f, -12f)
        )

        antennae.forEachIndexed { index, (startX, startY) ->
            val endX = if (index == 0) -15f else 15f
            val endY = -25f
            val waveAmount = sin(state.antennaWave * PI.toFloat() * 2) * 2

            // Draw wavy antenna
            val segments = 10
            for (i in 0 until segments) {
                val t = i.toFloat() / segments
                val x1 = startX + (endX - startX) * t
                val y1 = startY + (endY - startY) * t
                val wave1 = sin(t * PI.toFloat() + state.antennaWave * PI.toFloat() * 2) * 2

                val t2 = (i + 1).toFloat() / segments
                val x2 = startX + (endX - startX) * t2
                val y2 = startY + (endY - startY) * t2
                val wave2 = sin(t2 * PI.toFloat() + state.antennaWave * PI.toFloat() * 2) * 2

                canvas.drawLine(x1 + wave1, y1, x2 + wave2, y2, paint)
            }

            // Draw antenna tip
            paint.style = Paint.Style.FILL
            paint.color = Color.rgb(101, 67, 33)
            canvas.drawCircle(endX + waveAmount, endY, 2f, paint)
            paint.style = Paint.Style.STROKE
            paint.color = Color.rgb(139, 69, 19)
        }
    }

    private fun drawReactionIndicators(
        canvas: Canvas,
        state: CockroachAnimator.CockroachState,
        effects: ReactionEffects
    ) {
        val intensity = effects.getReactionIntensity()

        // Blink indicator
        if (intensity.blink > 0.3f) {
            paint.color = Color.argb((intensity.blink * 128).toInt(), 255, 200, 0)
            paint.style = Paint.Style.FILL
            canvas.drawCircle(state.x - 15 * state.scale, state.y - 10 * state.scale, 5 * state.scale, paint)
            canvas.drawCircle(state.x + 15 * state.scale, state.y - 10 * state.scale, 5 * state.scale, paint)
        }

        // Mouth open indicator
        if (intensity.mouthOpen > 0.3f) {
            paint.color = Color.argb((intensity.mouthOpen * 128).toInt(), 255, 150, 0)
            paint.style = Paint.Style.FILL
            canvas.drawOval(
                state.x - 8 * state.scale,
                state.y + 5 * state.scale,
                state.x + 8 * state.scale,
                state.y + 15 * state.scale,
                paint
            )
        }
    }

    private fun drawPerformanceStats(
        canvas: Canvas,
        metrics: PerformanceMetrics,
        fpsCounter: FPSCounter
    ) {
        paint.color = Color.argb(179, 0, 0, 0) // 70% black
        paint.style = Paint.Style.FILL
        canvas.drawRect(10f, 10f, 210f, 110f, paint)

        paint.color = Color.rgb(0, 217, 255) // Neon cyan
        paint.textSize = 12f
        paint.style = Paint.Style.FILL

        val statsText = listOf(
            "FPS: ${fpsCounter.fps}",
            "Frame: ${metrics.frameTime.toInt()}ms",
            "Memory: ${metrics.memoryUsage.toInt()}MB",
            "GPU: ${metrics.gpuLoad.toInt()}%"
        )

        statsText.forEachIndexed { index, text ->
            canvas.drawText(text, 20f, 30f + index * 18, paint)
        }
    }

    data class PerformanceMetrics(
        val frameTime: Float,
        val memoryUsage: Float,
        val gpuLoad: Float
    )

    private class FPSCounter {
        var fps = 0
        private var frameCount = 0
        private var lastTime = System.currentTimeMillis()

        fun update() {
            frameCount++
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= 1000) {
                fps = frameCount
                frameCount = 0
                lastTime = currentTime
            }
        }
    }
}
