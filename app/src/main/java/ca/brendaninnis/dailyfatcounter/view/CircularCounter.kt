package ca.brendaninnis.dailyfatcounter.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ca.brendaninnis.dailyfatcounter.R

class CircularCounter(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var progress = 0.67f
    private var rectF = RectF(0f, 0f, 0f, 0f)
    private val thiccness = context.resources.getDimension(R.dimen.counterThiccness)
    private val halfThiccness = thiccness * 0.5f
    private val thirdThiccness = thiccness * 0.33f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val colors = intArrayOf(ContextCompat.getColor(context, R.color.green),
        ContextCompat.getColor(context, R.color.yellow),
        ContextCompat.getColor(context, R.color.yellow),
        ContextCompat.getColor(context, R.color.green))

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF = RectF(0f, 0f, w.toFloat(), h.toFloat()).apply {
            inset(halfThiccness, halfThiccness)
        }
        val gradient = SweepGradient(w * 0.5f, h * 0.5f, colors, null)
        paint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        // call the super method to keep any drawing from the parent side.
        super.onDraw(canvas)

        drawProgressCircle(canvas)
        drawInnerCircle(canvas)
    }

    private fun drawInnerCircle(canvas: Canvas) {
        paint.strokeWidth = thirdThiccness
        canvas.drawOval(rectF, paint)
    }

    private fun drawProgressCircle(canvas: Canvas) {
        paint.strokeWidth = thiccness
        canvas.drawArc(rectF, CIRCLE_START_ANGLE, progress * CIRCLE_FULL_ROTATION, false, paint)
    }

    companion object {
        const val CIRCLE_START_ANGLE    = -90f
        const val CIRCLE_FULL_ROTATION  = 360f
    }
}