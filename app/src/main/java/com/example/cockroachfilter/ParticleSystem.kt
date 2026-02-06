package com.example.cockroachfilter

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.PI
import kotlin.math.sin

/**
 * Particle effects system for dust, fear, and motion trails
 */
class ParticleSystem(private val maxParticles: Int = 100) {
    data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var life: Float,
        val maxLife: Float,
        var size: Float,
        val color: Int,
        val type: ParticleType
    )

    enum class ParticleType {
        DUST, FEAR, TRAIL
    }

    private val particles = mutableListOf<Particle>()
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    /**
     * Create dust particles when cockroach walks
     */
    fun createDustParticles(x: Float, y: Float, count: Int = 3) {
        repeat(count) {
            if (particles.size >= maxParticles) return@repeat

            val angle = (Math.random() * PI * 2).toFloat()
            val speed = (Math.random() * 1 + 0.5).toFloat()

            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    life = 1f,
                    maxLife = (30 + Math.random() * 20).toFloat(),
                    size = (Math.random() * 2 + 1).toFloat(),
                    color = 0xC8B4A0.toInt() or (0xCC shl 24), // Brown with alpha
                    type = ParticleType.DUST
                )
            )
        }
    }

    /**
     * Create fear particles when cockroach runs away
     */
    fun createFearParticles(x: Float, y: Float, count: Int = 5) {
        repeat(count) {
            if (particles.size >= maxParticles) return@repeat

            val angle = (Math.random() * PI * 2).toFloat()
            val speed = (Math.random() * 2 + 1).toFloat()

            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    life = 1f,
                    maxLife = (20 + Math.random() * 15).toFloat(),
                    size = (Math.random() * 1.5 + 0.5).toFloat(),
                    color = 0xFF6464.toInt() or (0x99 shl 24), // Red with alpha
                    type = ParticleType.FEAR
                )
            )
        }
    }

    /**
     * Create motion trail particles
     */
    fun createTrailParticles(x: Float, y: Float, color: Int = 0x00D9FF.toInt() or (0x4D shl 24)) {
        if (particles.size >= maxParticles) return

        particles.add(
            Particle(
                x = x,
                y = y,
                vx = 0f,
                vy = 0f,
                life = 1f,
                maxLife = 15f,
                size = 3f,
                color = color,
                type = ParticleType.TRAIL
            )
        )
    }

    /**
     * Update all particles
     */
    fun update() {
        particles.removeAll { particle ->
            particle.life -= 1f / particle.maxLife
            particle.x += particle.vx
            particle.y += particle.vy

            // Apply gravity to dust
            if (particle.type == ParticleType.DUST) {
                particle.vy += 0.05f
            }

            particle.life <= 0
        }
    }

    /**
     * Draw all particles
     */
    fun draw(canvas: Canvas) {
        particles.forEach { particle ->
            paint.alpha = (particle.life * 255).toInt()
            paint.color = particle.color

            canvas.drawCircle(particle.x, particle.y, particle.size, paint)
        }
    }

    fun clear() {
        particles.clear()
    }

    fun getParticleCount(): Int = particles.size
}

/**
 * Reaction effects manager
 */
class ReactionEffects {
    private var blinkEffect = 0f
    private var mouthOpenEffect = 0f
    private var headShakeEffect = 0f

    fun updateReactions(
        eyesClosed: Float,
        mouthOpen: Float,
        headTurn: Float
    ) {
        blinkEffect = eyesClosed
        mouthOpenEffect = mouthOpen
        headShakeEffect = kotlin.math.abs(headTurn)
    }

    fun getReactionIntensity(): ReactionIntensity {
        return ReactionIntensity(
            blink = blinkEffect,
            mouthOpen = mouthOpenEffect,
            headShake = headShakeEffect
        )
    }

    data class ReactionIntensity(
        val blink: Float,
        val mouthOpen: Float,
        val headShake: Float
    )
}

/**
 * Screen shake effect
 */
class ScreenShakeEffect {
    private var intensity = 0f
    private var duration = 0

    fun trigger(intensity: Float = 1f, duration: Int = 10) {
        this.intensity = intensity
        this.duration = duration
    }

    fun update() {
        if (duration > 0) {
            duration--
        } else {
            intensity = 0f
        }
    }

    fun getOffset(): Pair<Float, Float> {
        if (intensity == 0f) return Pair(0f, 0f)

        return Pair(
            (Math.random() - 0.5f).toFloat() * intensity * 2,
            (Math.random() - 0.5f).toFloat() * intensity * 2
        )
    }
}
