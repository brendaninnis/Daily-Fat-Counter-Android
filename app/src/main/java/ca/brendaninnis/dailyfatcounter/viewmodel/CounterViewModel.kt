package ca.brendaninnis.dailyfatcounter.viewmodel

import androidx.databinding.*
import androidx.lifecycle.*
import ca.brendaninnis.dailyfatcounter.BR
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.datastore.DEFAULT_DAILY_TOTAL_FAT
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_HOUR
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_MINUTE
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class CounterViewModel(private val counterDataRepository: CounterDataRepository): ObservableViewModel() {
    var usedFat     = ObservableFloat(0.0f)
    var totalFat    = ObservableFloat(DEFAULT_DAILY_TOTAL_FAT)
    var resetHour   = ObservableInt(0)
    var resetMinute = ObservableInt(0)

    var progress: Float
        @Bindable get() = (usedFat.get() / totalFat.get())
        set(value) {
            usedFat.set(totalFat.get() * value)
        }

    var nextReset = counterDataRepository.nextResetFlow.asLiveData()

    init {
        viewModelScope.launch {
            counterDataRepository.counterDataFlow.first().let {
                usedFat.set(it.usedFat)
                totalFat.set(it.totalFat)
                resetHour.set(it.resetHour)
                resetMinute.set(it.resetMinute)
            }
            notifyPropertyChanged(BR.progress)
            observeAndPersistCounterData(counterDataRepository)
        }
    }

    private fun observeAndPersistCounterData(counterDataRepository: CounterDataRepository) {
        usedFat.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                (sender as? ObservableFloat)?.let { value ->
                    viewModelScope.launch {
                        counterDataRepository.updateUsedFat(value.get())
                    }
                }
            }
        })
        totalFat.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                (sender as? ObservableFloat)?.let { value ->
                    viewModelScope.launch {
                        counterDataRepository.updateTotalFat(value.get())
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

    fun setResetTime(millis: Long) {
        viewModelScope.launch {
            val hours   = millis / MILLISECONDS_PER_HOUR
            val minutes = millis % MILLISECONDS_PER_MINUTE
            resetHour.set(hours.toInt())
            resetMinute.set(minutes.toInt())
            counterDataRepository.updateResetTime(resetHour.get(), resetMinute.get())
        }
    }

    class CounterViewModelFactory(private val counterDataRepository: CounterDataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CounterViewModel(counterDataRepository) as T
        }
    }
}