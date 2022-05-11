package ca.brendaninnis.dailyfatcounter.viewmodel

import android.content.SharedPreferences
import androidx.databinding.Observable
import androidx.databinding.ObservableFloat

class Persistence(private val sharedPreferences: SharedPreferences,
                  private val viewModel: CounterViewModel) {
    fun start(restorePreferences: Boolean) {
        if (restorePreferences) {
            restorePreferences(sharedPreferences, viewModel)
        }
        observeAndPersistDouble(viewModel.usedFat, USED_FAT_PREF_KEY, sharedPreferences)
        observeAndPersistDouble(viewModel.totalFat, TOTAL_FAT_PREF_KEY, sharedPreferences)
    }

    private fun restorePreferences(preferences: SharedPreferences, viewModel: CounterViewModel) {
        if (preferences.contains(USED_FAT_PREF_KEY)) {
            viewModel.usedFat.set(preferences.getFloat(USED_FAT_PREF_KEY, 0f))
        }
        if (preferences.contains(TOTAL_FAT_PREF_KEY)) {
            viewModel.totalFat.set(
                preferences.getFloat(
                    TOTAL_FAT_PREF_KEY,
                    CounterViewModel.DEFAULT_DAILY_TOTAL_FAT
                )
            )
        }
    }

    private fun observeAndPersistDouble(float: ObservableFloat,
                                key: String,
                                preferences: SharedPreferences) {
        float.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                preferences.edit().apply {
                    putFloat(key, (sender as ObservableFloat).get())
                    apply()
                }
            }
        })
    }

    companion object {
        const val USED_FAT_PREF_KEY     = "used_fat"
        const val TOTAL_FAT_PREF_KEY    = "total_fat"
    }
}