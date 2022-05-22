package ca.brendaninnis.dailyfatcounter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ca.brendaninnis.dailyfatcounter.databinding.ActivityMainBinding
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.math.SECONDS_PER_DAY
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel
import ca.brendaninnis.dailyfatcounter.viewmodel.HistoryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.concurrent.timer

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
    private val calendar by lazy {
        Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
    }
    private var counterTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil
            .setContentView(this, R.layout.activity_main)

        setupBottomNavigationView(binding)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val now = System.currentTimeMillis()
            checkDailyFatReset(now)
            startResetTimer(now)
        }
    }

    override fun onPause() {
        super.onPause()
        stopResetTimer()
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
            recordDailyFatValues(timestamp)
        }
    }

    private fun startResetTimer(now: Long) {
        val nowOffset = now + TimeZone.getDefault().rawOffset
        val nowSinceMidnight = nowOffset % SECONDS_PER_DAY
        val resetTime = counterViewModel.resetTime.get()
        val fireAt = if (nowSinceMidnight < resetTime) {
            resetTime - nowSinceMidnight
        } else {
            SECONDS_PER_DAY - nowSinceMidnight + resetTime
        }
        counterTimer = timer("fat_counter_timer",
            initialDelay = fireAt,
            period = SECONDS_PER_DAY,
            action = {
                recordDailyFatValues(System.currentTimeMillis())
            }
        )
    }

    private fun stopResetTimer() {
        counterTimer?.cancel()
    }

    private fun recordDailyFatValues(timestamp: Long) {
        calendar.time = Date(timestamp)
        val dailyFatRecord = DailyFatRecord.createDailyFatRecord(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            counterViewModel.usedFat.get(),
            counterViewModel.totalFat.get()
        )
        historyViewModel.addDailyFatRecord(dailyFatRecord) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
        counterViewModel.resetUsedFat()
    }
}