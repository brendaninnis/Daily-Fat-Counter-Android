package ca.brendaninnis.dailyfatcounter.datamodel

import ca.brendaninnis.dailyfatcounter.view.formatters.MDYFormatter
import ca.brendaninnis.dailyfatcounter.view.formatters.MonthFormatter
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class DailyFatRecord(val id: Int,
                     val start: Long,
                     val usedFat: Float,
                     val totalFat: Float) {
    val json: String
        get() = Gson().toJson(this)

    @Transient private var date: Date? = null
    get() {
        if (field == null) {
            field = Date(start)
        }
        return field
    }

    val dateLabel: String
        get() {
            return MDYFormatter.format(date!!)
        }

    val monthLabel: String
        get() {
            return MonthFormatter.format(date!!)
        }

    companion object {
        const val TAG = "DailyFatRecord"

        @JvmStatic
        fun fromJson(json: String): ArrayList<DailyFatRecord> = Gson()
            .fromJson(json, Array<DailyFatRecord>::class.java).toCollection(ArrayList())
    }
}