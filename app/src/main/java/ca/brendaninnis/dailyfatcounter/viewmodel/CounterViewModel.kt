package ca.brendaninnis.dailyfatcounter.viewmodel

import androidx.databinding.ObservableFloat

class CounterViewModel: ObservableViewModel() {
    val usedFat = ObservableFloat(0.0f)
    val totalFat = ObservableFloat(DEFAULT_DAILY_TOTAL_FAT)

    fun addFat(grams: Float) {
        usedFat.set(usedFat.get() + grams)
    }

    companion object {
        const val DEFAULT_DAILY_TOTAL_FAT = 45.0f
    }
}