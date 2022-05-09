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
            notifyPropertyChanged(BR.progress)
        }

    init {
        observeAndNotifyProgress(usedFat)
        observeAndNotifyProgress(totalFat)
    }

    private fun observeAndNotifyProgress(field: Observable) {
        field.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                notifyPropertyChanged(BR.progress)
            }
        })
    }

    fun addFat(grams: Float) {
        usedFat.set(usedFat.get() + grams)
    }

    companion object {
        const val DEFAULT_DAILY_TOTAL_FAT = 45.0f
    }
}