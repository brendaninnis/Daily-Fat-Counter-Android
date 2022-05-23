package ca.brendaninnis.dailyfatcounter.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.databinding.HistoryHeaderBinding
import ca.brendaninnis.dailyfatcounter.databinding.HistoryItemBinding
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord

class HistoryHeaderViewHolder(
    private val binding: HistoryHeaderBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(header: String) {
        binding.monthLabel.text = header
    }

    companion object {
        fun create(parent: ViewGroup): HistoryHeaderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.history_header, parent, false)
            val binding = HistoryHeaderBinding.bind(view)
            return HistoryHeaderViewHolder(binding)
        }
    }
}