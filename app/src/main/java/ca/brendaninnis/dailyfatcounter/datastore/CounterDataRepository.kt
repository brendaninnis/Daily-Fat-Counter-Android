package ca.brendaninnis.dailyfatcounter.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val DEFAULT_DAILY_TOTAL_FAT = 45.0f

data class CounterData(
    val usedFat: Float,
    val totalFat: Float
)

class CounterDataRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferenceKeys {
        val USED_FAT    = floatPreferencesKey("used_fat")
        val TOTAL_FAT   = floatPreferencesKey("total_fat")
        val RESET_TIME  = longPreferencesKey("reset_time")
        val LAST_CHECK  = longPreferencesKey("last_check")
    }

    val counterDataFlow: Flow<CounterData> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapCounterData(preferences)
        }

    suspend fun updateUsedFat(usedFat: Float) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USED_FAT] = usedFat
        }
    }

    suspend fun updateTotalFat(totalFat: Float) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TOTAL_FAT] = totalFat
        }
    }

    private fun mapCounterData(preferences: Preferences): CounterData = CounterData(
        usedFat = preferences[PreferenceKeys.USED_FAT] ?: 0f,
        totalFat = preferences[PreferenceKeys.TOTAL_FAT] ?: DEFAULT_DAILY_TOTAL_FAT
    )

    companion object {
        private val TAG = "CounterDataRepository"
    }
}