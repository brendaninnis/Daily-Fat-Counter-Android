package ca.brendaninnis.dailyfatcounter.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord
import ca.brendaninnis.dailyfatcounter.view.HistoryItemViewHolder
import ca.brendaninnis.dailyfatcounter.view.HistoryHeaderViewHolder

class HistoryRow(val monthHeader: String?, val dailyFatRecord: DailyFatRecord?)

class HistoryAdapter: ListAdapter<HistoryRow, RecyclerView.ViewHolder>(HISTORY_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.history_header -> {
                return HistoryHeaderViewHolder.create(parent)
            }
        }
        return HistoryItemViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position).monthHeader?.let {
            return R.layout.history_header
        }
        return R.layout.history_item
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val historyItem = getItem(position) ?: return
        when (holder) {
            is HistoryHeaderViewHolder -> {
                historyItem.monthHeader?.let {
                    holder.bind(it)
                }
            }
            is HistoryItemViewHolder -> {
                historyItem.dailyFatRecord?.let {
                    holder.bind(it)
                }
            }
        }

    }

    companion object {
        private val HISTORY_COMPARATOR = object : DiffUtil.ItemCallback<HistoryRow>() {
            override fun areItemsTheSame(oldItem: HistoryRow,
                                         newItem: HistoryRow
            ): Boolean =
                oldItem.monthHeader == newItem.monthHeader &&
                oldItem.dailyFatRecord?.id == newItem.dailyFatRecord?.id

            override fun areContentsTheSame(oldItem: HistoryRow,
                                            newItem: HistoryRow
            ): Boolean =
                oldItem.monthHeader == newItem.monthHeader &&
                oldItem.dailyFatRecord?.json == newItem.dailyFatRecord?.json
        }
    }
}