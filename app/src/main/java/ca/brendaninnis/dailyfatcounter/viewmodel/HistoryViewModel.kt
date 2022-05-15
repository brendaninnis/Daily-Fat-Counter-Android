package ca.brendaninnis.dailyfatcounter.viewmodel

import androidx.databinding.ObservableArrayList
import ca.brendaninnis.dailyfatcounter.datamodel.DailyFatRecord

class HistoryViewModel: ObservableViewModel() {
    var history: ObservableArrayList<DailyFatRecord> = ObservableArrayList()
}