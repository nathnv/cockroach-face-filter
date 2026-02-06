package com.example.cockroachfilter

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.comparisons.minOf
import kotlin.comparisons.maxOf

/**
 * Cockroach animation and movement system
 * Handles realistic cockroach behavior, leg animation, and reactions
 */
class CockroachAnimator(
    initialX: Float,
    initialY: Float,
    private val config: CockroachConfig = CockroachConfig()
) {
    data class CockroachState(
        val x: Float,
        val y: Float,
        val rotation: Float,
        val scale: Float,
        val legPhase: Float,
        val antennaWave: Float,
        val isRunning: Boolean,
        val direction: Int
    )

    data class CockroachConfig(
        val speed: Float = 2f,
        val legAnimationSpeed: Float = 0.15f,
        val antennaWaveSpeed: Float = 0.08f,
        val pauseFrequency: Float = 0.02f,
        val directionChangeFrequency: Float = 0.01f,
        val minScale: Float = 0.8f,
        val maxScale: Float = 1.2f
    )

    private var state = CockroachState(
        x = initialX,
        y = initialY,
        rotation = 0f,
        scale = 1f,
        legPhase = 0f,
        antennaWave = 0f,
        isRunning = false,
        direction = if (Math.random() > 0.5) 1 else -1
    )

    private var isPaused = false
    private var pauseTimer = 0
    private val maxPauseTime = 60
    private var currentSpeed = config.speed

    /**
     * Update cockroach position and animation state
     */
    fun update(
        faceWidth: Float,
        faceHeight: Float,
        faceX: Float,
        faceY: Float,
        reactions: FacialExpressions? = null
    ): CockroachState {
        // Handle pause state
        if (isPaused) {
            pauseTimer--
            if (pauseTimer <= 0) {
                isPaused = false
            }
        } else {
            // Random pause
            if (Math.random() < config.pauseFrequency) {
                isPaused = true
                pauseTimer = (Math.random() * maxPauseTime).toInt()
            }

            // Random direction change
            if (Math.random() < config.directionChangeFrequency) {
                state = state.copy(direction = state.direction * -1)
            }

            // Move cockroach
            if (!isPaused) {
                var newX = state.x + currentSpeed * state.direction
                
                // Bounce off edges
                val minX = faceX + 20
                val maxX = faceX + faceWidth - 20
                if (newX < minX || newX > maxX) {
                    state = state.copy(direction = state.direction * -1)
                    newX = max(minX, min(maxX, newX))
                }

                // Slight vertical movement
                var newY = state.y + (sin(System.currentTimeMillis() * 0.001) * 0.5).toFloat()
                val minY = faceY + 20f
                val maxY = faceY + faceHeight - 20f
                newY = newY.coerceIn(minY, maxY)

                state = state.copy(x = newX, y = newY)
            }
        }

        // Update rotation based on direction
        state = state.copy(rotation = if (state.direction > 0) 0f else PI.toFloat())

        // Update leg animation
        state = state.copy(legPhase = (state.legPhase + config.legAnimationSpeed) % 1f)

        // Update antenna wave
        state = state.copy(antennaWave = (state.antennaWave + config.antennaWaveSpeed) % 1f)

        // Handle reactions
        if (reactions != null) {
            val isRunning = reactions.eyesClosed > 0.5f || reactions.mouthOpen > 0.5f
            state = state.copy(isRunning = isRunning)
            
            currentSpeed = if (isRunning) 4f else config.speed

            // Head turn affects position
            state = state.copy(x = state.x + reactions.headTurn * 2)
        }

        // Slight scale variation based on animation
        val baseScale = (config.minScale + config.maxScale) / 2
        val scaleVariation = sin(state.legPhase * PI.toFloat() * 2) * 
            (config.maxScale - config.minScale) * 0.1f
        state = state.copy(scale = baseScale + scaleVariation)

        return state
    }

    fun getState(): CockroachState = state

    fun reset(x: Float, y: Float) {
        state = CockroachState(
            x = x,
            y = y,
            rotation = 0f,
            scale = 1f,
            legPhase = 0f,
            antennaWave = 0f,
            isRunning = false,
            direction = if (Math.random() > 0.5) 1 else -1
        )
        isPaused = false
        pauseTimer = 0
    }
}

/**
 * Calculate leg positions for cockroach
 */
fun calculateLegPositions(
    centerX: Float,
    centerY: Float,
    scale: Float,
    rotation: Float,
    legPhase: Float
): List<Pair<Float, Float>> {
    val legs = mutableListOf<Pair<Float, Float>>()
    val legLength = 15 * scale
    val bodyWidth = 20 * scale

    // 6 legs: 3 on each side
    val legAngles = listOf(
        -PI / 3,      // Front left
        -PI / 2,      // Middle left
        (-2 * PI) / 3, // Back left
        PI / 3,       // Front right
        PI / 2,       // Middle right
        (2 * PI) / 3  // Back right
    )

    legAngles.forEachIndexed { index, angle ->
        val phase = (legPhase + (index % 2) * 0.5f) % 1f
        val legBend = sin(phase * PI.toFloat()) * 5 * scale

        val legX = (centerX + cos(angle + rotation) * (bodyWidth + legBend)).toFloat()
        val legY = (centerY + sin(angle + rotation) * (bodyWidth + legBend)).toFloat()

        legs.add(Pair(legX, legY))
    }

    return legs
}

/**
 * Calculate antenna positions
 */
fun calculateAntennaPositions(
    centerX: Float,
    centerY: Float,
    scale: Float,
    rotation: Float,
    antennaWave: Float
): List<Pair<Float, Float>> {
    val antennaLength = 25 * scale
    val antennaSpread = PI.toFloat() / 6

    val leftAntennaAngle = rotation + PI.toFloat() / 2 + antennaSpread
    val rightAntennaAngle = rotation + PI.toFloat() / 2 - antennaSpread

    val waveAmount = sin(antennaWave * PI.toFloat() * 2) * 0.1f

    return listOf(
        Pair(
            centerX + cos(leftAntennaAngle + waveAmount) * antennaLength,
            centerY + sin(leftAntennaAngle + waveAmount) * antennaLength
        ),
        Pair(
            centerX + cos(rightAntennaAngle - waveAmount) * antennaLength,
            centerY + sin(rightAntennaAngle - waveAmount) * antennaLength
        )
    )
}
