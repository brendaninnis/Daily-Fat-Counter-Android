package ca.brendaninnis.dailyfatcounter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ca.brendaninnis.dailyfatcounter.databinding.ActivityMainBinding
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel
import ca.brendaninnis.dailyfatcounter.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

const val DATA_STORE_NAME = "daily_fat_counter"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATA_STORE_NAME)

class MainActivity : AppCompatActivity() {
    private val historyFile by lazy {
        File(filesDir, "history.data")
    }
    private val counterDataRepository by lazy {
        CounterDataRepository(dataStore)
    }
    private val counterViewModel: CounterViewModel by viewModels {
        CounterViewModel.CounterViewModelFactory(counterDataRepository)
    }
    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModel.HistoryViewModelFactory(historyFile)
    }
    private val dailyFatResetHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var scheduledResetTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil
            .setContentView(this, R.layout.activity_main)

        setupBottomNavigationView(binding)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            checkDailyFatReset(System.currentTimeMillis())
            counterViewModel.nextReset.observe(this@MainActivity) { nextReset ->
                startResetTimer(nextReset)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopResetTimer()
        counterViewModel.nextReset.removeObservers(this)
    }

    private fun setupBottomNavigationView(binding: ActivityMainBinding) {
        binding.navHostFragment.getFragment<NavHostFragment>().navController.let { navController ->
            binding.bottomNavigationView.setupWithNavController(navController)
        }
    }

    private suspend fun checkDailyFatReset(timestamp: Long) {
        if (counterDataRepository.checkResetTimeElapsed(timestamp)) {
            recordDailyFatValues()
        } else {
            if (!counterDataRepository.calculateAndSetNextReset()) {
                // Reset time hasn't changed, start the timer "manually"
                startResetTimer(counterViewModel.nextReset.value ?: 0L)
            }
        }
    }

    private fun startResetTimer(nextReset: Long) {
        if (scheduledResetTime == nextReset) {
            return
        }
        stopResetTimer()
        Log.d(TAG, "Next reset scheduled for ${Date(nextReset)}")
        dailyFatResetHandler.postDelayed({
            lifecycleScope.launch {
                recordDailyFatValues()
            }
        }, nextReset - Date().time)
        scheduledResetTime = nextReset
    }

    private fun stopResetTimer() {
        dailyFatResetHandler.removeCallbacksAndMessages(null)
        scheduledResetTime = 0L
    }

    private suspend fun recordDailyFatValues() {
        historyViewModel.addDailyFatRecord(
            counterDataRepository.calculateLastReset(
                counterViewModel.resetHour.get(),
                counterViewModel.resetMinute.get(),
                counterViewModel.nextReset.value ?: Date().time
            ),
            counterViewModel.usedFat.get(),
            counterViewModel.totalFat.get()
        )
        if (!counterDataRepository.calculateAndSetNextReset()) {
            Log.w(TAG, "Daily reset elapsed but the calculated reset time hasn't changed")
        }
        counterViewModel.resetUsedFat()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}