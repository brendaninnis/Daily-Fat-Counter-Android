package ca.brendaninnis.dailyfatcounter.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_HOUR
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_MINUTE
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class TimePickerPreference(context: Context, attrs: AttributeSet): Preference(context, attrs) {
    private var defaultValue = 0L
    private val valueFromDataStore: Long
        get() = preferenceDataStore?.getLong(key, defaultValue) ?: defaultValue
    private val hours
        get() = valueFromDataStore / MILLISECONDS_PER_HOUR
    private val minutes
        get() = (valueFromDataStore % MILLISECONDS_PER_HOUR) / MILLISECONDS_PER_MINUTE

    init {
        widgetLayoutResource = R.layout.time_picker_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        findTimeButton(holder)?.apply {
            text = formatTime()
        }
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        (defaultValue as? Int)?.let {
            this.defaultValue = it.toLong()
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInt(index, 0)
    }

    fun showPicker(fragmentManager: FragmentManager) {
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hours.toInt())
            .setMinute(minutes.toInt())
            .setTitleText("Select reset time")
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    val time = (hour * MILLISECONDS_PER_HOUR +
                                minute * MILLISECONDS_PER_MINUTE).toLong()
                    preferenceDataStore?.putLong(key, time)
                    notifyChanged()
                }
                show(fragmentManager, null)
            }
    }

    private fun findTimeButton(holder: PreferenceViewHolder): TextView? {
        return holder.findViewById(R.id.time_picker_value) as? TextView
    }

    private fun formatTime(): String {
        val period: String
        var hoursForDisplay = hours
        if (hoursForDisplay > 12) {
            period = "p.m."
            hoursForDisplay -= 12
        } else if (hoursForDisplay == 12L) {
            period = "p.m."
        } else if (hoursForDisplay == 0L) {
            hoursForDisplay = 12
            period = "a.m."
        } else {
            period = "a.m."
        }
        return String.format("%d:%02d %s", hoursForDisplay, minutes, period)
    }
}