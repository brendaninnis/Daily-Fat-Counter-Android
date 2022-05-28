package ca.brendaninnis.dailyfatcounter.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.NumberPicker
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import ca.brendaninnis.dailyfatcounter.R

class NumberPickerPreference(context: Context, attrs: AttributeSet): Preference(context, attrs) {
    private var defaultValue = 0
    private val valueFromDataStore: Int
        get() = preferenceDataStore?.getInt(key, defaultValue) ?: defaultValue

    init {
        widgetLayoutResource = R.layout.number_picker_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        findNumberPicker(holder)?.apply {
            minValue = 0
            maxValue = 999
            value = valueFromDataStore
            findSummary(holder)?.text = formatSummary(value)
            setOnValueChangedListener { _, _, newVal ->
                preferenceDataStore?.putInt(key, newVal)
                findSummary(holder)?.text = formatSummary(newVal)
            }
        }
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        (defaultValue as? Int)?.let {
            this.defaultValue = it
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInt(index, 0)
    }

    private fun findNumberPicker(holder: PreferenceViewHolder): NumberPicker? {
        return holder.findViewById(R.id.total_fat_number_picker) as? NumberPicker
    }

    private fun findSummary(holder: PreferenceViewHolder): TextView? {
        return holder.findViewById(android.R.id.summary) as? TextView
    }

    private fun formatSummary(value: Int): CharSequence {
        return String.format("%.1fg", value.toFloat())
    }
}