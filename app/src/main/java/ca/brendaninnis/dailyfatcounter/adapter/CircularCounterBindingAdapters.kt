package ca.brendaninnis.dailyfatcounter.adapter

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import ca.brendaninnis.dailyfatcounter.view.CircularCounter

object CircularCounterBindingAdapters {
    @BindingAdapter("custom:progress")
    @JvmStatic fun setProgressFloat(view: CircularCounter, value: Float) {
        if (view.progress != value) {
            view.progress = value
        }
    }

    @InverseBindingAdapter(attribute = "custom:progress")
    @JvmStatic fun getProgressFloat(view: CircularCounter): Float {
        return view.progress
    }

    @BindingAdapter("progressAttrChanged")
    @JvmStatic fun setProgressListener(view: CircularCounter, listener: InverseBindingListener) {
        view.addProgressWatcher(object : CircularCounter.Companion.ProgressWatcher {
            override fun progressChanged(newValue: Float) {
                listener.onChange()
            }
        })
    }
}