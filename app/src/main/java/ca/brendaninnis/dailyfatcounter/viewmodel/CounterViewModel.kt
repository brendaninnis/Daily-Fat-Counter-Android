package ca.brendaninnis.dailyfatcounter.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableFloat
import ca.brendaninnis.dailyfatcounter.BR

class CounterViewModel: ObservableViewModel() {
    var usedFat = ObservableFloat(0.0f)
    var totalFat = ObservableFloat(DEFAULT_DAILY_TOTAL_FAT)
    var progress: Float
        @Bindable get() = (usedFat.get() / totalFat.get())
        set(value) {
            usedFat.set(totalFat.get() * value)
        }

    fun addFat(grams: Float) {
        usedFat.set(usedFat.get() + grams)
        notifyPropertyChanged(BR.progress)
    }

    companion object {
        const val DEFAULT_DAILY_TOTAL_FAT = 45.0f
    }
}