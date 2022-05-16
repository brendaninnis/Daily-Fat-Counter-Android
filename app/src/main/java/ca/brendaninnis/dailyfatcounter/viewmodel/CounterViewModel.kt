package ca.brendaninnis.dailyfatcounter.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.brendaninnis.dailyfatcounter.BR
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.datastore.DEFAULT_DAILY_TOTAL_FAT
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class CounterViewModel(counterDataRepository: CounterDataRepository): ObservableViewModel() {
    var usedFat = ObservableFloat(0.0f)
    var totalFat = ObservableFloat(DEFAULT_DAILY_TOTAL_FAT)
    var resetTime = ObservableLong(0L)
    var progress: Float
        @Bindable get() = (usedFat.get() / totalFat.get())
        set(value) {
            usedFat.set(totalFat.get() * value)
        }

    init {
        viewModelScope.launch {
            counterDataRepository.counterDataFlow.first().let {
                usedFat.set(it.usedFat)
                totalFat.set(it.totalFat)
                resetTime.set(it.resetTime)
            }
            notifyPropertyChanged(BR.progress)
            observeAndPersistCounterData(counterDataRepository)
        }
    }

    private fun observeAndPersistCounterData(counterDataRepository: CounterDataRepository) {
        usedFat.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                (sender as? ObservableFloat)?.let {
                    viewModelScope.launch {
                        counterDataRepository.updateUsedFat(it.get())
                    }
                }
            }
        })
        totalFat.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                (sender as? ObservableFloat)?.let {
                    viewModelScope.launch {
                        counterDataRepository.updateTotalFat(it.get())
                    }
                }
            }
        })
    }

    fun addFat(grams: Float) {
        usedFat.set(usedFat.get() + grams)
        notifyPropertyChanged(BR.progress)
    }

    fun resetUsedFat() {
        usedFat.set(0f)
        notifyPropertyChanged(BR.progress)
    }

    fun updateTotalFat(grams: Float) {
        totalFat.set(grams)
        notifyPropertyChanged(BR.progress)
    }

    class CounterViewModelFactory(private val counterDataRepository: CounterDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CounterViewModel(counterDataRepository) as T
        }
    }
}