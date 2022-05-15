package ca.brendaninnis.dailyfatcounter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel
import ca.brendaninnis.dailyfatcounter.viewmodel.HistoryViewModel
import ca.brendaninnis.dailyfatcounter.extensions.getOrDefault
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

const val DATA_STORE_NAME = "daily_fat_counter"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATA_STORE_NAME)

class MainActivity : AppCompatActivity() {
    private val historyFile by lazy {
        File(filesDir, "history.data")
    }
    private val counterViewModel: CounterViewModel by viewModels {
        CounterViewModel.CounterViewModelFactory(CounterDataRepository(dataStore))
    }
    private val historyViewModel: HistoryViewModel by viewModels()
    private val counterTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        counterViewModel.progress

        // Setup the Bottom Navigation Bar
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .setupWithNavController(findNavController(R.id.nav_host_fragment))

        if (savedInstanceState == null) {
            historyViewModel.viewModelScope.launch {
                historyViewModel.load(historyFile)
//                counterViewModel.start()
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        lifecycleScope.launch {
//            checkDailyFatReset(System.currentTimeMillis())
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//    }
//
//    private suspend fun checkDailyFatReset(timestamp: Long) {
//        if (resetTimeElapsed(timestamp)) {
//
//        } else {
//
//        }
//    }
//
//    private suspend fun resetTimeElapsed(timestamp: Long): Boolean {
//        val lastCheck = dataStore.getOrDefault(LAST_CHECK_KEY, 0L)
//        if (lastCheck <= 0L) {
//            return false
//        }
//        val resetTime = dataStore.data.map { preferences ->
//            preferences[RESET_TIME_KEY] ?: 0L
//        }
//        resetTime.collect {
//
//        }
//
//        val nowDays = timestamp + TimeZone.getDefault().rawOffset
//    }
//
//    companion object {
//
//    }
}