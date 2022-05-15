package ca.brendaninnis.dailyfatcounter.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.viewModelScope
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord
import ca.brendaninnis.dailyfatcounter.extensions.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HistoryViewModel: ObservableViewModel() {
    var history: ObservableArrayList<DailyFatRecord> = ObservableArrayList()

    suspend fun load(historyFile: File) {
        val savedHistory = withContext(Dispatchers.IO) {
            if (!historyFile.exists()) {
                return@withContext arrayOf()
            }
            DailyFatRecord.fromJson(historyFile.readText())
        }
        history.addAll(savedHistory)
    }

    fun save(historyFile: File) {
        viewModelScope.launch {
            historyFile.writeBytes(history.json.toByteArray())
        }
    }
}
