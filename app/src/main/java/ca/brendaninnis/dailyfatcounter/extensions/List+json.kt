package ca.brendaninnis.dailyfatcounter.extensions

import com.google.gson.Gson

val <T> List<T>.json: String
    get() {
        return Gson().toJson(this)
    }
