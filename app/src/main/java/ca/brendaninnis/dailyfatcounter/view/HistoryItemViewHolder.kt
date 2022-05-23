package ca.brendaninnis.dailyfatcounter.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.databinding.HistoryItemBinding
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord


class HistoryItemViewHolder(
    private val binding: HistoryItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(record: DailyFatRecord) {
        binding.dateLabel.text = record.dateLabel
        binding.graph.progress = record.usedFat / record.totalFat
    }

    companion object {
        fun create(parent: ViewGroup): HistoryItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_item, parent, false)
            val binding = HistoryItemBinding.bind(view)
            return HistoryItemViewHolder(binding)
        }
    }
}