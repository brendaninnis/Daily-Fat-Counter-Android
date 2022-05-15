package ca.brendaninnis.dailyfatcounter.datamodel

import android.icu.text.DateFormatSymbols
import com.google.gson.Gson

const val DAY_MASK      = 0x000000FF
const val MONTH_MASK    = 0x0000FF00
const val YEAR_MASK     = 0xFFFF0000

const val MONTH_SHIFT   = 8
const val YEAR_SHIFT    = 16

class DailyFatRecord(private val id: Int,
                     private val usedFat: Float,
                     private val totalFat: Float) {
    val json: String
        get() = Gson().toJson(this)

    val dateLabel: String
        get() {
            val year = (id.toLong() and YEAR_MASK) shr YEAR_SHIFT
            val day = (id and DAY_MASK)
            return String.format("%s %02d, %04d", monthLabel, day, year)
        }

    private val monthLabel: String
        get() {
            val month = (id and MONTH_MASK) shr MONTH_SHIFT
            return DateFormatSymbols().months[month - 1]
        }

    companion object {
        @JvmStatic
        fun fromJson(json: String): Array<DailyFatRecord> = Gson()
            .fromJson(json, Array<DailyFatRecord>::class.java)

        fun createDailyFatRecord(year: Int,
                                 month: Int,
                                 day: Int,
                                 usedFat: Float,
                                 totalFat: Float): DailyFatRecord {
            val id = year shl YEAR_SHIFT + month shl MONTH_SHIFT + day
            return DailyFatRecord(id, usedFat, totalFat)
        }
    }
}