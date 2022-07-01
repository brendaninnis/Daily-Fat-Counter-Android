package ca.brendaninnis.dailyfatcounter.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.dataStore
import ca.brendaninnis.dailyfatcounter.datastore.CounterDataRepository
import ca.brendaninnis.dailyfatcounter.datastore.SettingsDataStore
import ca.brendaninnis.dailyfatcounter.view.preference.TimePickerPreference
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
            RESET_FAT_PREF_KEY -> {
                counterViewModel.resetUsedFat()
                Toast.makeText(requireContext(),
                    "Today's fat used reset to 0.0g",
                    Toast.LENGTH_SHORT).show()
            }
            RESET_TIME_PREF_KEY -> {
                (preference as TimePickerPreference).showPicker(childFragmentManager)
            }
            GIVE_FEEDBACK_PREF_KEY -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(
                        "market://details?id=${requireContext().packageName}")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://play.google.com/store/apps/details?id=${requireContext().packageName}")))
                }
            }
            REPORT_BUG_PREF_KEY -> {
                Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("brendaninnis@icloud.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Daily Fat Counter: Bug report")
                    putExtra(Intent.EXTRA_TEXT, "[ Please describe what you expected to happen, " +
                            "what you did and what actually happened. ]")
                    try {
                        startActivity(Intent.createChooser(this, "Send bug report..."))
                    } catch (exception: ActivityNotFoundException) {
                        Toast.makeText(
                            requireContext(),
                            "There are no email clients installed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    companion object {
        private const val RESET_FAT_PREF_KEY        = "reset_fat_now"
        private const val RESET_TIME_PREF_KEY       = "reset_time_pref_key"
        private const val GIVE_FEEDBACK_PREF_KEY    = "give_feedback"
        private const val REPORT_BUG_PREF_KEY       = "report_a_bug"
    }
}