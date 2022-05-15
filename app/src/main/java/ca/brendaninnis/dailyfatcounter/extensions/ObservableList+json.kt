package ca.brendaninnis.dailyfatcounter.extensions

import androidx.databinding.ObservableList
import com.google.gson.Gson

val <T> ObservableList<T>.json: String
    get() {
        return Gson().toJson(this)
    }
