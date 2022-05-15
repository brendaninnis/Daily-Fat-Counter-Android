package ca.brendaninnis.dailyfatcounter.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableFloat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.brendaninnis.dailyfatcounter.BR
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.datastore.DEFAULT_DAILY_TOTAL_FAT
import ca.brendaninnis.dailyfatcounter.extensions.getOrDefault
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class CounterViewModel(counterDataRepository: CounterDataRepository): ObservableViewModel() {
    var usedFat = ObservableFloat(0.0f)
    var totalFat = ObservableFloat(DEFAULT_DAILY_TOTAL_FAT)
    var progress: Float
        @Bindable get() = (usedFat.get() / totalFat.get())
        set(value) {
            usedFat.set(totalFat.get() * value)
            Log.w("TESTY", "set used fat to ${totalFat.get() * value}")
        }

    init {
        collectCounterDataIntoObservable(counterDataRepository)
        observeAndPersistCounterData(counterDataRepository)
    }

    private fun collectCounterDataIntoObservable(counterDataRepository: CounterDataRepository) {
        viewModelScope.launch {
            counterDataRepository.counterDataFlow.collect { counterData ->
                var didChange = false
                if (usedFat.get() != counterData.usedFat) {
                    Log.w("TESTY", "used fat did change to ${counterData.usedFat}")
                    usedFat.set(counterData.usedFat)
                    didChange = true
                }
                if (totalFat.get() != counterData.totalFat) {
                    totalFat.set(counterData.totalFat)
                    didChange = true
                }
                if (didChange) {
                    notifyPropertyChanged(BR.progress)
                }
            }
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

    class CounterViewModelFactory(private val counterDataRepository: CounterDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CounterViewModel(counterDataRepository) as T
        }
    }
}