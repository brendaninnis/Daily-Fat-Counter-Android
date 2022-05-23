package ca.brendaninnis.dailyfatcounter.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import ca.brendaninnis.dailyfatcounter.math.SECONDS_PER_DAY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.*

const val DEFAULT_DAILY_TOTAL_FAT = 45.0f

data class CounterData(
    val usedFat: Float,
    val totalFat: Float,
    val resetTime: Long
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

    suspend fun checkResetTimeElapsed(timestamp: Long): Boolean {
        var retVal = false
        dataStore.edit { preferences ->
            val lastCheck = preferences[PreferenceKeys.LAST_CHECK] ?: 0L
            val resetTime = preferences[PreferenceKeys.RESET_TIME] ?: 0L
            val nowDays = (timestamp + TimeZone.getDefault().rawOffset - resetTime) / SECONDS_PER_DAY
            val thenDays = (lastCheck + TimeZone.getDefault().rawOffset - resetTime) / SECONDS_PER_DAY
            retVal = nowDays > thenDays && lastCheck > 0
            preferences[PreferenceKeys.LAST_CHECK] = timestamp
        }
        return retVal
    }

    private fun mapCounterData(preferences: Preferences): CounterData = CounterData(
        usedFat = preferences[PreferenceKeys.USED_FAT] ?: 0f,
        totalFat = preferences[PreferenceKeys.TOTAL_FAT] ?: DEFAULT_DAILY_TOTAL_FAT,
        resetTime = preferences[PreferenceKeys.RESET_TIME] ?: 0L
    )

    companion object {
        private val TAG = "CounterDataRepository"
    }
}