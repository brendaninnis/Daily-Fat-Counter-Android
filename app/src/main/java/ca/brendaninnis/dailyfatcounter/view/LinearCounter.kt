package ca.brendaninnis.dailyfatcounter.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import ca.brendaninnis.dailyfatcounter.R

open class LinearCounter(context: Context,
                         attrs: AttributeSet) : View(context, attrs),
                                                ValueAnimator.AnimatorUpdateListener  {
    protected var animator: ValueAnimator? = null
    protected val thiccness = context.resources.getDimension(R.dimen.counterThiccness)
    protected val halfThiccness = thiccness * 0.5f
    protected val thirdThiccness = thiccness * 0.33f
    protected val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    protected var _progress: Float
    open var progress: Float
        get() = _progress
        set(value) {
            if (animator == null && value != _progress) {
                animator = ValueAnimator.ofFloat(progress, value).apply {
                    duration = ANIMATION_DURATION
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener(this@LinearCounter)
                    start()
                }
            }
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LinearCounter, 0, 0).apply {
            try {
                _progress = getFloat(R.styleable.LinearCounter_progress, 0f)
            } finally {
                recycle()
            }
        }
    }

    @SuppressLint("DrawAllocation") // Must allocate new SweepGradient with colors for each draw
    override fun onDraw(canvas: Canvas) {
        LinearGradient(0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            intArrayOf(
                Colors.getStartGradientColor(context, progress),
                Colors.getEndGradientColor(context, progress),
            ),
            null,
            Shader.TileMode.REPEAT).let {
                paint.shader = it
        }

        drawProgressLine(canvas)
        drawInnerLine(canvas)
    }

    override fun onAnimationUpdate(animator: ValueAnimator) {
        _progress = animator.animatedValue as Float
        invalidate()
    }

    private fun drawProgressLine(canvas: Canvas) {
        if (progress <= 0) {
            return
        }
        paint.strokeWidth = thiccness
        canvas.drawLine(halfThiccness + paddingStart,
            height * 0.5f,
            width.toFloat() * progress.coerceAtMost(1.0f) - halfThiccness - paddingEnd,
            height * 0.5f,
            paint)
    }

    private fun drawInnerLine(canvas: Canvas) {
        paint.strokeWidth = thirdThiccness
        canvas.drawLine(halfThiccness + paddingStart,
            height * 0.5f,
            width.toFloat() - halfThiccness - paddingEnd,
            height * 0.5f,
            paint)
    }

    companion object {
        const val ANIMATION_DURATION = 350L
    }
}
