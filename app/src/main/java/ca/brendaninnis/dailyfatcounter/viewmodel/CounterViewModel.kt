package ca.brendaninnis.dailyfatcounter.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableFloat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.brendaninnis.dailyfatcounter.BR
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CounterViewModel(dataStore: DataStore<Preferences>): ObservableViewModel() {
    var usedFat = ObservableFloat(0.0f)
    var totalFat = ObservableFloat(DEFAULT_DAILY_TOTAL_FAT)
    var progress: Float
        @Bindable get() = (usedFat.get() / totalFat.get())
        set(value) {
            usedFat.set(totalFat.get() * value)
        }

    init {
        viewModelScope.launch {
            usedFat.set(getFloatOrDefault(dataStore, USED_FAT_KEY, 0f))
            totalFat.set(getFloatOrDefault(dataStore, TOTAL_FAT_KEY, DEFAULT_DAILY_TOTAL_FAT))
            notifyPropertyChanged(BR.progress)
            observeAndPersistFloat(usedFat, USED_FAT_KEY, dataStore)
            observeAndPersistFloat(totalFat, TOTAL_FAT_KEY, dataStore)
        }
    }

    private suspend fun getFloatOrDefault(dataStore: DataStore<Preferences>,
                                  key: Preferences.Key<Float>,
                                  default: Float): Float {
        return dataStore.data.map { preferences ->
            preferences[key] ?: default
        }.first()
    }

    private fun observeAndPersistFloat(float: ObservableFloat,
                                       key: Preferences.Key<Float>,
                                       preferences: DataStore<Preferences>) {
        float.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                viewModelScope.launch {
                    preferences.edit { prefs ->
                        prefs[key] = (sender as ObservableFloat).get()
                    }
                }
            }
        })
    }

    fun addFat(grams: Float) {
        usedFat.set(usedFat.get() + grams)
        notifyPropertyChanged(BR.progress)
    }

    class CounterViewModelFactory(private val preferences: DataStore<Preferences>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CounterViewModel(preferences) as T
        }
    }

    companion object {
        const val DEFAULT_DAILY_TOTAL_FAT = 45.0f
        val USED_FAT_KEY = floatPreferencesKey("used_fat")
        val TOTAL_FAT_KEY = floatPreferencesKey("total_fat")
    }
}