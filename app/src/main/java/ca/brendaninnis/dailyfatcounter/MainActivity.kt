package ca.brendaninnis.dailyfatcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the Bottom Navigation Bar
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .setupWithNavController(findNavController(R.id.nav_host_fragment))
    }
}