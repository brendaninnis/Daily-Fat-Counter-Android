package ca.brendaninnis.dailyfatcounter.view.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import ca.brendaninnis.dailyfatcounter.R


class DFCPreferenceCategory(context: Context, attrs: AttributeSet): PreferenceCategory(context, attrs) {
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.findViewById<TextView>(android.R.id.title)
            .setTextColor(ContextCompat.getColor(context, R.color.blue))
    }
}