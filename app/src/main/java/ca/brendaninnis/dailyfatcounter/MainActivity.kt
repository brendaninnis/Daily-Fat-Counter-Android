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
import java.time.ZonedDateTime
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
    private var dailyFatResetHandler = Handler(Looper.getMainLooper())
    private var resumed = false
    private var scheduledResetTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil
            .setContentView(this, R.layout.activity_main)

        setupBottomNavigationView(binding)

        Log.d(TAG, "Observe next reset")
        counterViewModel.nextReset.observe(this) { nextReset ->
            startResetTimer(nextReset)
        }
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        lifecycleScope.launch {
            checkDailyFatReset(System.currentTimeMillis())
        }
    }

    override fun onPause() {
        super.onPause()
        stopResetTimer()
        resumed = false
    }

    private fun setupBottomNavigationView(binding: ActivityMainBinding) {
        binding.navHostFragment.getFragment<NavHostFragment>().navController.let { navController ->
            navController.addOnDestinationChangedListener { _, destination, arguments ->
                when (destination.id) {
                    R.id.historyFragment -> {
                        arguments?.putSerializable("historyFile", historyFile)
                    }
                }
            }
            binding.bottomNavigationView.setupWithNavController(navController)
        }
    }

    private suspend fun checkDailyFatReset(timestamp: Long) {
        if (counterDataRepository.checkResetTimeElapsed(timestamp)) {
            recordDailyFatValues()
        } else {
            counterDataRepository.calculateAndSetNextReset()
        }
    }

    private fun startResetTimer(nextReset: Long) {
        if (scheduledResetTime == nextReset || !resumed) {
            return
        }
        Log.d(TAG, "Next Reset at ${Date(nextReset)}")
        stopResetTimer()
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
        counterDataRepository.calculateAndSetNextReset()
        counterViewModel.resetUsedFat()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}