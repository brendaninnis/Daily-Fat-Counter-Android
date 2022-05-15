package ca.brendaninnis.dailyfatcounter.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableFloat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.viewModelScope
import ca.brendaninnis.dailyfatcounter.dataStore
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

class Persistence(private val context: Context,
                  private val historyViewModel: HistoryViewModel) {
    private val historyFile = File(context.filesDir, "history.data")

    fun start() {
        loadHistory(historyViewModel)
    }

    private fun loadHistory(historyViewModel: HistoryViewModel) {
        historyViewModel.viewModelScope.launch {
            val history = withContext(Dispatchers.IO) {
                if (!historyFile.exists()) {
                    return@withContext arrayOf()
                }
                DailyFatRecord.fromJson(historyFile.readText())
            }
            historyViewModel.history.addAll(history)
        }
    }

    companion object {
        val USED_FAT_PREF_KEY     = floatPreferencesKey("used_fat")
        val TOTAL_FAT_PREF_KEY    = floatPreferencesKey("total_fat")
    }
}