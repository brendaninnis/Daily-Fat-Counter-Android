package ca.brendaninnis.dailyfatcounter.view

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import ca.brendaninnis.dailyfatcounter.R
import java.lang.Math.round
import kotlin.math.roundToInt

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
        return Color.rgb(
            getTransition(
                Color.red(fromColor),
                Color.red(toColor),
                percent
            ),
            getTransition(
                Color.green(fromColor),
                Color.green(toColor),
                percent
            ),
            getTransition(
                Color.blue(fromColor),
                Color.blue(toColor),
                percent
            )
        )
    }

    private fun getTransition(start: Int,
                              end: Int,
                              percent: Float) = start + ((end - start) * percent).roundToInt()
}