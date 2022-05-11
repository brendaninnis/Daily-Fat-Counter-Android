package ca.brendaninnis.dailyfatcounter.view

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import ca.brendaninnis.dailyfatcounter.R

object Colors {
    fun getStartGradientColor(context: Context, progress: Float): Int {
        val startTransformPercent = if (progress > 1) (progress - 1f).coerceAtMost(1f) else 0f
        return getColor(
            ContextCompat.getColor(context, R.color.green),
            ContextCompat.getColor(context, R.color.red),
            startTransformPercent
        )
    }

    fun getEndGradientColor(context: Context, progress: Float): Int {
        val endTransformPercent = if (progress > 2) (progress - 2f).coerceAtMost(1f) else 0f
        return getColor(
            ContextCompat.getColor(context, R.color.yellow),
            ContextCompat.getColor(context, R.color.red),
            endTransformPercent
        )
    }

    private fun getColor(fromColor: Int, toColor: Int, percent: Float): Int {
        return Color.valueOf(
            getTransition(
                Color.red(fromColor).toFloat() / 255.0f,
                Color.red(toColor).toFloat() / 255.0f,
                percent
            ),
            getTransition(
                Color.green(fromColor).toFloat() / 255.0f,
                Color.green(toColor).toFloat() / 255.0f,
                percent
            ),
            getTransition(
                Color.blue(fromColor).toFloat() / 255.0f,
                Color.blue(toColor).toFloat() / 255.0f,
                percent
            )
        ).toArgb()
    }

    private fun getTransition(start: Float,
                              end: Float,
                              percent: Float) = start + (end - start) * percent
}