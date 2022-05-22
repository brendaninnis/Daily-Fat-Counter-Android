package ca.brendaninnis.dailyfatcounter.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.databinding.FragmentHistoryBinding
import ca.brendaninnis.dailyfatcounter.view.HistoryAdapter
import ca.brendaninnis.dailyfatcounter.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {
    private val args: HistoryFragmentArgs by navArgs()
    private val adapter = HistoryAdapter()
    val viewModel: HistoryViewModel by activityViewModels {
        HistoryViewModel.HistoryViewModelFactory(args.historyFile)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil
            .inflate<FragmentHistoryBinding>(inflater, R.layout.fragment_history, container, false)
            .apply {
                historyRecycler.addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                )
                historyRecycler.adapter = adapter
            }
        viewModel.historyLiveData.observe(viewLifecycleOwner) { history ->
            adapter.submitList(history)
        }
        return binding.root
    }
}