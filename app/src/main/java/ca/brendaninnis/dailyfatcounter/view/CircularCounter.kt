package ca.brendaninnis.dailyfatcounter.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.animation.*
import ca.brendaninnis.dailyfatcounter.math.Geometry
import ca.brendaninnis.dailyfatcounter.R
import kotlin.math.atan


class CircularCounter(context: Context, attrs: AttributeSet) : View(context, attrs), ValueAnimator.AnimatorUpdateListener {
    private var rectF = RectF(0f, 0f, 0f, 0f)
    private var circleOrigin = PointF(0f, 0f)
    private val thiccness = context.resources.getDimension(R.dimen.counterThiccness)
    private val halfThiccness = thiccness * 0.5f
    private val thirdThiccness = thiccness * 0.33f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private var animator: ValueAnimator? = null
    private var touchLocation = PointF(0f, 0f)
    private var lastAngle = 0f
    private var lastQuadrant: Geometry.Quadrant = Geometry.Quadrant.ONE

    private var progressWatchers: MutableSet<ProgressWatcher> = mutableSetOf()
    private var _progress: Float
    var progress: Float
        get() = _progress
        set(value) {
            if (value != _progress) {
                animator?.cancel()
                animator = ValueAnimator.ofFloat(progress, value).apply {
                    duration = 350
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener(this@CircularCounter)
                    start()
                }
            }
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CircularCounter, 0, 0).apply {
            try {
                _progress = getFloat(R.styleable.CircularCounter_progress, 0f)
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF = RectF(0f, 0f, w.toFloat(), h.toFloat()).apply {
            inset(halfThiccness + paddingStart, halfThiccness + paddingTop)
        }
        circleOrigin = PointF(rectF.centerX(), rectF.centerY())
    }

    @SuppressLint("ClickableViewAccessibility") // TODO: Add accessibility features to this app
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        val opposite: Float
        val adjacent: Float
        val quadrantOffset: Float

        when (event?.action) {
            ACTION_DOWN, ACTION_MOVE -> {
                touchLocation.x = event.x
                touchLocation.y = event.y

                val newQuadrant = Geometry.quadrant(touchLocation, circleOrigin)
                when (newQuadrant) {
                    Geometry.Quadrant.ONE -> {
                        quadrantOffset = 0f
                        opposite = touchLocation.x - circleOrigin.x
                        adjacent = circleOrigin.y - touchLocation.y
                    }
                    Geometry.Quadrant.TWO -> {
                        quadrantOffset = Math.PI.toFloat() * 0.5f
                        opposite = touchLocation.y - circleOrigin.y
                        adjacent = touchLocation.x - circleOrigin.x
                    }
                    Geometry.Quadrant.THREE -> {
                        quadrantOffset = Math.PI.toFloat()
                        opposite = circleOrigin.x - touchLocation.x
                        adjacent = touchLocation.y - circleOrigin.y
                    }
                    Geometry.Quadrant.FOUR -> {
                        quadrantOffset = Math.PI.toFloat() * 1.5f
                        opposite = circleOrigin.y - touchLocation.y
                        adjacent = circleOrigin.x - touchLocation.x
                    }
                }
                val newAngle = atan(opposite / adjacent) + quadrantOffset

                if (event.action == ACTION_DOWN) {
                    lastAngle = newAngle
                    lastQuadrant = newQuadrant
                    return true
                }

                var rotationOffset = 0f
                if (newQuadrant == Geometry.Quadrant.ONE && lastQuadrant == Geometry.Quadrant.FOUR) {
                    rotationOffset = Geometry.RADIANS_PER_ROTATION
                } else if (newQuadrant == Geometry.Quadrant.FOUR && lastQuadrant == Geometry.Quadrant.ONE) {
                    rotationOffset = -1.0f * Geometry.RADIANS_PER_ROTATION
                }
                val angleChange = newAngle - lastAngle + rotationOffset
                val progressChange = angleChange / Geometry.RADIANS_PER_ROTATION
                _progress += progressChange
                if (_progress < 0f) {
                    _progress = 0f
                }
                invalidate()
                notifyProgressWatchers()

                lastAngle = newAngle
                lastQuadrant = newQuadrant

                return true
            }
            ACTION_UP -> {
                performClick()
                return true
            }
        }
        return false
    }

    @SuppressLint("DrawAllocation") // Must allocate new SweepGradient with colors for each draw
    override fun onDraw(canvas: Canvas) {
        // call the super method to keep any drawing from the parent side.
        super.onDraw(canvas)

        SweepGradient(width * 0.5f,
                      height * 0.5f,
                      intArrayOf(
                          Colors.getStartGradientColor(context, _progress),
                          Colors.getEndGradientColor(context, _progress),
                          Colors.getEndGradientColor(context, _progress),
                          Colors.getStartGradientColor(context, _progress)
                      ),
                      null).let {
            paint.shader = it
        }

        drawProgressCircle(canvas)
        drawInnerCircle(canvas)
    }

    fun addProgressWatcher(watcher: ProgressWatcher) {
        progressWatchers.add(watcher)
    }

    fun removeProgressWatcher(watcher: ProgressWatcher) {
        progressWatchers.remove(watcher)
    }

    private fun notifyProgressWatchers() {
        progressWatchers.forEach { watcher ->
            watcher.progressChanged(progress)
        }
    }

    private fun drawInnerCircle(canvas: Canvas) {
        paint.strokeWidth = thirdThiccness
        canvas.drawOval(rectF, paint)
    }

    private fun drawProgressCircle(canvas: Canvas) {
        paint.strokeWidth = thiccness
        canvas.drawArc(rectF, CIRCLE_START_ANGLE, _progress * CIRCLE_FULL_ROTATION, false, paint)
    }

    // MARK: ValueAnimator.AnimatorUpdateListener methods
    override fun onAnimationUpdate(animator: ValueAnimator) {
        _progress = animator.animatedValue as Float
        invalidate()
    }

    companion object {
        const val CIRCLE_START_ANGLE    = -90f
        const val CIRCLE_FULL_ROTATION  = 360f

        interface ProgressWatcher {
            fun progressChanged(newValue: Float)
        }

    }
}