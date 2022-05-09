package ca.brendaninnis.dailyfatcounter.math

import android.graphics.PointF

class Geometry {
    enum class Quadrant {
        ONE, TWO, THREE, FOUR
    }

    companion object {
        const val RADIANS_PER_ROTATION = Math.PI.toFloat() * 2.0f

        fun quadrant(point: PointF, circleOrigin: PointF): Quadrant =
            if (point.x >= circleOrigin.x) {
                if (point.y < circleOrigin.y) {
                    Quadrant.ONE
                } else {
                    Quadrant.TWO
                }
            } else {
                if (point.y >= circleOrigin.y) {
                    Quadrant.THREE
                } else {
                    Quadrant.FOUR
                }
            }
    }
}