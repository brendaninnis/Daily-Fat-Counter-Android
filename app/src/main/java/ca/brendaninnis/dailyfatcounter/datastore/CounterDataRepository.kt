package ca.brendaninnis.dailyfatcounter.datastore

import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_MINUTE
import ca.brendaninnis.dailyfatcounter.math.MILLISECONDS_PER_SECOND
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.math.min

const val DEFAULT_DAILY_TOTAL_FAT = 45.0f

data class CounterData(
    val usedFat     : Float,
    val totalFat    : Float,
    val resetHour   : Int,
    val resetMinute : Int
)

class CounterDataRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferenceKeys {
        val USED_FAT        = floatPreferencesKey("used_fat")
        val TOTAL_FAT       = floatPreferencesKey("total_fat")
        val RESET_HOUR      = intPreferencesKey("reset_hour")
        val RESET_MINUTE    = intPreferencesKey("reset_minute")
        val NEXT_RESET      = longPreferencesKey("next_reset")
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

    val nextResetFlow: Flow<Long> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferenceKeys.NEXT_RESET] ?: 0L
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
            val nextReset = preferences[PreferenceKeys.NEXT_RESET] ?: 0L
            retVal = nextReset in 1..timestamp
        }
        return retVal
    }

    suspend fun updateResetTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.RESET_HOUR] = hour
            preferences[PreferenceKeys.RESET_MINUTE] = minute
            preferences[PreferenceKeys.NEXT_RESET] = calculateNextReset(hour, minute)
        }
    }

    suspend fun calculateAndSetNextReset() {
        dataStore.edit { preferences ->
            val hour = preferences[PreferenceKeys.RESET_HOUR] ?: 0
            val minute = preferences[PreferenceKeys.RESET_MINUTE] ?: 0
            preferences[PreferenceKeys.NEXT_RESET] = calculateNextReset(hour, minute)
        }
    }

    fun calculateLastReset(hour: Int, minute: Int, resetTime: Long): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var dateTime = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(resetTime),
                ZoneId.systemDefault()
            )
            dateTime = dateTime.minusDays(1)
            return dateTime
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .toEpochSecond() * MILLISECONDS_PER_SECOND
        } else {
            Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).apply {
                add(Calendar.DAY_OF_MONTH, -1)
                set(Calendar.HOUR, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                return time.time
            }
        }
    }

    private fun calculateNextReset(hour: Int, minute: Int): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var dateTime = ZonedDateTime.now()
            if (dateTime.hour > hour
                || (dateTime.hour == hour && dateTime.minute > minute)) {
                dateTime = dateTime.plusDays(1)
            }
            return dateTime
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .toEpochSecond() * MILLISECONDS_PER_SECOND
        } else {
            Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()).apply {
                if (get(Calendar.HOUR) > hour ||
                    (get(Calendar.HOUR) == hour && get(Calendar.MINUTE) > minute)) {
                    // Past reset time for the current day, get the reset time for tomorrow
                    add(Calendar.DAY_OF_MONTH, 1)
                }
                set(Calendar.HOUR, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                return time.time
            }
        }
    }

    private fun mapCounterData(preferences: Preferences): CounterData = CounterData(
        usedFat     = preferences[PreferenceKeys.USED_FAT]      ?: 0f,
        totalFat    = preferences[PreferenceKeys.TOTAL_FAT]     ?: DEFAULT_DAILY_TOTAL_FAT,
        resetHour   = preferences[PreferenceKeys.RESET_HOUR]    ?: 0,
        resetMinute = preferences[PreferenceKeys.RESET_MINUTE]  ?: 0
    )

    companion object {
        private const val TAG = "CounterDataRepository"
    }
}