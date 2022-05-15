package ca.brendaninnis.dailyfatcounter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel
import ca.brendaninnis.dailyfatcounter.viewmodel.HistoryViewModel
import ca.brendaninnis.dailyfatcounter.viewmodel.Persistence
import com.google.android.material.bottomnavigation.BottomNavigationView

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "daily_fat_counter")

class MainActivity : AppCompatActivity() {

    private val counterViewModel: CounterViewModel by viewModels {
        CounterViewModel.CounterViewModelFactory(dataStore)
    }
    private val historyViewModel: HistoryViewModel by viewModels()
    private val persistence: Persistence by lazy {
        Persistence(this, historyViewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        counterViewModel.progress

        // Setup the Bottom Navigation Bar
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .setupWithNavController(findNavController(R.id.nav_host_fragment))

        // Monitor and write fat counter data to disk
        if (savedInstanceState == null) {
            persistence.start()
        }
    }
}