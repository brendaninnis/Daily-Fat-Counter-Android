package ca.brendaninnis.dailyfatcounter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.dataStore
import ca.brendaninnis.dailyfatcounter.databinding.FragmentCounterBinding
import ca.brendaninnis.dailyfatcounter.viewmodel.CounterViewModel
import java.util.Date

class CounterFragment : Fragment() {
    val viewModel: CounterViewModel by activityViewModels {
        CounterViewModel.CounterViewModelFactory(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        DataBindingUtil
            .inflate<FragmentCounterBinding>(inflater, R.layout.fragment_counter, container, false)
            .apply {
                viewModel = this@CounterFragment.viewModel
                date = Date()
                return root
        }
    }
}