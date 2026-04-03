package com.epicgera.vtrae.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.random.Random

/**
 * VTR Æ: "Threads of the Æther"
 * A highly optimized, 60fps capable Canvas animation replacing standard spinners.
 * Renders subtle, glowing cyan and white lines weaving against a dark translucent background.
 */
class AetherLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val threadCount = 12
    private val threads = Array(threadCount) { AetherThread() }
    
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#101010")
        alpha = 200 // Translucent dark background
        style = Paint.Style.FILL
    }
    
    private val threadPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val threadPath = Path()

    // For indeterminate animation
    private var phase = 0f
    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, Math.PI.toFloat() * 2f).apply {
        duration = 4000L
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener { animation ->
            phase = animation.animatedValue as Float
            postInvalidateOnAnimation()
        }
    }
    
    // For progress integration (0 to 100)
    var progress: Int = 0
        set(value) {
            field = value.coerceIn(0, 100)
            postInvalidateOnAnimation()
        }
        
    var isIndeterminate: Boolean = true
        set(value) {
            field = value
            if (value && visibility == VISIBLE) {
                animator.start()
            } else {
                animator.cancel()
            }
        }

    init {
        // Required for shadow layers (glow effect) if hardware acceleration limits it in older APIs.
        // On modern APIs, thin blurred paths work fine hardware accelerated.
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (visibility == VISIBLE && isIndeterminate) {
            animator.start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE && isIndeterminate) {
            animator.start()
        } else {
            animator.cancel()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val w = width.toFloat()
        val h = height.toFloat()
        
        // Background
        canvas.drawRect(0f, 0f, w, h, bgPaint)
        
        val centerY = h / 2f
        val amplitudeBase = h * 0.35f
        
        // Progress defines how far the threads extend horizontally if not indeterminate
        val drawWidth = if (isIndeterminate) w else w * (progress / 100f)
        
        for (i in 0 until threadCount) {
            val thread = threads[i]
            
            threadPaint.color = thread.color
            threadPaint.strokeWidth = thread.thickness
            // Glow effect
            threadPaint.setShadowLayer(thread.glowRadius, 0f, 0f, thread.color)
            threadPaint.alpha = thread.baseAlpha
            
            threadPath.reset()
            
            val segments = 20
            val segmentWidth = drawWidth / segments
            
            var px = 0f
            var py = centerY + (Math.sin((phase * thread.speed + thread.phaseOffset).toDouble()) * amplitudeBase * thread.amplitudeModifier).toFloat()
            threadPath.moveTo(px, py)
            
            for (j in 1..segments) {
                px = j * segmentWidth
                val timeFactor = phase * thread.speed + thread.phaseOffset + (px / w) * thread.frequency
                py = centerY + (Math.sin(timeFactor.toDouble()) * amplitudeBase * thread.amplitudeModifier).toFloat()
                
                threadPath.lineTo(px, py)
            }
            
            canvas.drawPath(threadPath, threadPaint)
        }
    }

    // Preallocated object to hold thread parameters avoiding object creation in onDraw
    private class AetherThread {
        val speed: Float = Random.nextFloat() * 1.5f + 0.5f
        val phaseOffset: Float = Random.nextFloat() * Math.PI.toFloat() * 2f
        val amplitudeModifier: Float = Random.nextFloat() * 0.8f + 0.2f
        val frequency: Float = Random.nextFloat() * 4f + 1f
        val thickness: Float = Random.nextFloat() * 2f + 1f
        val glowRadius: Float = Random.nextFloat() * 6f + 2f
        
        val baseAlpha: Int = Random.nextInt(100, 255)
        
        val color: Int = if (Random.nextFloat() > 0.7f) {
            Color.WHITE
        } else {
            // Flix palette: warm cinematic hues matching the app theme
            Color.parseColor(arrayOf("#E50914", "#E87C03", "#FFD700", "#FF4444").random())
        }
    }
}

