package ca.brendaninnis.dailyfatcounter.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord
import ca.brendaninnis.dailyfatcounter.extensions.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class HistoryViewModel(private val historyFile: File): ObservableViewModel() {
    var historyLiveData = MutableLiveData<List<DailyFatRecord>>(listOf())
    private val initialLoadJob: Job
    val historyIsEmptyLiveData: MediatorLiveData<Int> by lazy {
        MediatorLiveData<Int>().apply {
            addSource(historyLiveData) {
                historyIsEmptyLiveData.value = visibleIfEmpty(it)
            }
        }
    }

    init {
        initialLoadJob = viewModelScope.launch {
            historyLiveData.value = load(historyFile)
        }
    }

    fun addDailyFatRecord(start: Long,
                          usedFat: Float,
                          totalFat: Float) {
        viewModelScope.launch {
            initialLoadJob.join()
            historyLiveData.value?.toMutableList()?.let { history ->
                history.add(0, DailyFatRecord(history.size, start, usedFat, totalFat))
                save(history)
                historyLiveData.value = history
                Log.d(TAG, "Daily Fat #${history.count()} Added for ${Date(start)} $usedFat/$totalFat")
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

    private fun <T> visibleIfEmpty(list: List<T>): Int {
        return if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    class HistoryViewModelFactory(private val historyFile: File) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(historyFile) as T
        }
    }

    companion object {
        const val TAG = "HistoryViewModel"
    }
}
