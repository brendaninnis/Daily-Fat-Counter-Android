package ca.brendaninnis.dailyfatcounter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.databinding.FragmentHistoryBinding
import ca.brendaninnis.dailyfatcounter.adapter.HistoryAdapter
import ca.brendaninnis.dailyfatcounter.adapter.HistoryRow
import ca.brendaninnis.dailyfatcounter.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {
    private val args: HistoryFragmentArgs by navArgs()
    private val adapter = HistoryAdapter()
    val viewModel: HistoryViewModel by activityViewModels {
        HistoryViewModel.HistoryViewModelFactory(args.historyFile)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil
            .inflate<FragmentHistoryBinding>(inflater, R.layout.fragment_history, container, false)
            .apply {
                viewModel = this@HistoryFragment.viewModel
                lifecycleOwner = this@HistoryFragment.viewLifecycleOwner
                historyRecycler.addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                )
                historyRecycler.adapter = adapter
            }
        observeHistoryAndUpdateListAdapter()
        return binding.root
    }

    private fun observeHistoryAndUpdateListAdapter() {
        viewModel.historyLiveData.observe(viewLifecycleOwner) { history ->
            val list = ArrayList<HistoryRow>()
            var currentMonth = ""
            history.forEach { dailyFatRecord ->
                if (dailyFatRecord.monthLabel != currentMonth) {
                    currentMonth = dailyFatRecord.monthLabel
                    list.add(HistoryRow(currentMonth, null))
                }
                list.add(HistoryRow(null, dailyFatRecord))
            }
            adapter.submitList(list)
        }
    }
}