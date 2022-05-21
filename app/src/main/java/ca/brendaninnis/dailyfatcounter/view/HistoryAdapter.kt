package ca.brendaninnis.dailyfatcounter.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord

class HistoryAdapter: ListAdapter<DailyFatRecord, DailyFatRecordViewHolder>(HISTORY_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyFatRecordViewHolder {
        return DailyFatRecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DailyFatRecordViewHolder, position: Int) {
        val dailyFatRecord = getItem(position)
        if (dailyFatRecord != null) {
            holder.bind(dailyFatRecord)
        }
    }

    companion object {
        private val HISTORY_COMPARATOR = object : DiffUtil.ItemCallback<DailyFatRecord>() {
            override fun areItemsTheSame(oldItem: DailyFatRecord,
                                         newItem: DailyFatRecord): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DailyFatRecord,
                                            newItem: DailyFatRecord): Boolean =
                oldItem.json == newItem.json
        }
    }
}