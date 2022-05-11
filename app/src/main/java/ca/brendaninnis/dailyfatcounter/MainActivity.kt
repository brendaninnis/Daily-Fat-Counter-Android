package ca.brendaninnis.dailyfatcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel
import ca.brendaninnis.dailyfatcounter.viewmodel.Persistence
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()
    private val persistence: Persistence by lazy {
        Persistence(getSharedPreferences("DailyFatCounter", MODE_PRIVATE), viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the Bottom Navigation Bar
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .setupWithNavController(findNavController(R.id.nav_host_fragment))

        // Monitor and write fat counter data to disk
        persistence.start(savedInstanceState == null)
    }
}