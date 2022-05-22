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
                if (history.isNotEmpty() && dailyFatRecord.id == history[0].id) {
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
        arrayListOf(
            DailyFatRecord(0x07E60709, 25f, 45f),
            DailyFatRecord(0x07E6070A, 145f, 45f),
            DailyFatRecord(0x07E6070B, 105f, 45f),
            DailyFatRecord(0x07E6070C, 30f, 45f),
            DailyFatRecord(0x07E6070D, 45f, 45f),
            DailyFatRecord(0x07E6070E, 70f, 45f),
            DailyFatRecord(0x07E6070F, 39f, 45f),

            DailyFatRecord(0x07E6080A, 145f, 45f),
            DailyFatRecord(0x07E6080C, 30f, 45f),
            DailyFatRecord(0x07E6080F, 39f, 45f),
            DailyFatRecord(0x07E60809, 55f, 45f),
            DailyFatRecord(0x07E6080D, 45f, 45f),
            DailyFatRecord(0x07E6080E, 10f, 45f),
            DailyFatRecord(0x07E6080B, 105f, 45f),

            DailyFatRecord(0x07E6090E, 10f, 45f),
            DailyFatRecord(0x07E6090D, 45f, 45f),
            DailyFatRecord(0x07E6090C, 30f, 45f),
            DailyFatRecord(0x07E60909, 25f, 45f),
            DailyFatRecord(0x07E6090B, 105f, 45f),
            DailyFatRecord(0x07E6090F, 39f, 45f),
            DailyFatRecord(0x07E6090A, 145f, 45f),
        )
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
