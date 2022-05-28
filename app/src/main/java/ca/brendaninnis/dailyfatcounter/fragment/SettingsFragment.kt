package ca.brendaninnis.dailyfatcounter.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.dataStore
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.datastore.SettingsDataStore
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel

class SettingsFragment : PreferenceFragmentCompat() {
    private val counterViewModel: CounterViewModel by activityViewModels {
        CounterViewModel.CounterViewModelFactory(CounterDataRepository(requireContext().dataStore))
    }
    private val dataStore by lazy {
        SettingsDataStore(counterViewModel)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val preferenceManager = preferenceManager
        preferenceManager.preferenceDataStore = dataStore
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "reset_fat_now" -> {
                counterViewModel.resetUsedFat()
                Toast.makeText(requireContext(),
                    "Today's fat used reset to 0.0g",
                    Toast.LENGTH_SHORT).show()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}