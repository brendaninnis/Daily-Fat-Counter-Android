package ca.brendaninnis.dailyfatcounter.datastore

import androidx.preference.PreferenceDataStore
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_HOUR
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_MINUTE
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel

class SettingsDataStore(private val counterViewModel: CounterViewModel): PreferenceDataStore() {
    override fun putInt(key: String?, value: Int) {
        when (key) {
            TOTAL_FAT_PREF_KEY -> {
                counterViewModel.updateTotalFat(value.toFloat())
            }
        }
    }

    override fun putLong(key: String?, value: Long) {
        when (key) {
            RESET_TIME_PREF_KEY -> {
                counterViewModel.setResetTime(value)
            }
        }
    }

    override fun getInt(key: String?, defValue: Int): Int {
        when (key) {
            TOTAL_FAT_PREF_KEY -> {
                return counterViewModel.totalFat.get().toInt()
            }
        }
        return super.getInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        when (key) {
            RESET_TIME_PREF_KEY -> {
                return counterViewModel.resetMinute.get().toLong() * MILLISECONDS_PER_MINUTE +
                       counterViewModel.resetHour.get() * MILLISECONDS_PER_HOUR
            }
        }
        return super.getLong(key, defValue)
    }

    companion object {
        const val TAG = "SettingsDataStore"

        const val TOTAL_FAT_PREF_KEY    = "total_fat_pref_key"
        const val RESET_TIME_PREF_KEY   = "reset_time_pref_key"
    }
}