package ca.brendaninnis.dailyfatcounter.viewmodel

import android.widget.Toast
import androidx.lifecycle.*
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord
import ca.brendaninnis.dailyfatcounter.extensions.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HistoryViewModel(private val historyFile: File): ObservableViewModel() {
    var historyLiveData = MutableLiveData<List<DailyFatRecord>>(listOf())
    private val initialLoadJob: Job

    init {
        initialLoadJob = viewModelScope.launch {
            historyLiveData.value = load(historyFile)
        }
    }

    fun addDailyFatRecord(dailyFatRecord: DailyFatRecord,
                          errorCallback: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            initialLoadJob.join()
            historyLiveData.value?.toMutableList()?.let { history ->
                if (dailyFatRecord.id == history[0].id) {
                    errorCallback?.let { callback ->
                        withContext(Dispatchers.Main) {
                            callback("Daily fat already recorded for ${dailyFatRecord.dateLabel}")
                        }
                    }
                    return@let
                }
                history.add(0, dailyFatRecord)
                save(history)
            }
        }
    }

    private suspend fun load(historyFile: File) = withContext(Dispatchers.IO) {
        if (!historyFile.exists()) {
            return@withContext ArrayList()
        }
        DailyFatRecord.fromJson(historyFile.readText())
    }

    private suspend fun save(history: List<DailyFatRecord>) {
        withContext(Dispatchers.IO) {
            historyFile.writeBytes(history.json.toByteArray())
        }
    }

    class HistoryViewModelFactory(private val historyFile: File) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(historyFile) as T
        }
    }
}
