package ca.brendaninnis.dailyfatcounter.extensions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

suspend fun <T> DataStore<Preferences>.getOrDefault(key: Preferences.Key<T>, default: T): T {
    return data.map { preferences ->
        preferences[key] ?: default
    }.first()
}
