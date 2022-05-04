package ca.brendaninnis.dailyfatcounter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import ca.brendaninnis.dailyfatcounter.R
import ca.brendaninnis.dailyfatcounter.view.CircularCounter

class CounterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_counter, container, false)
        val counter = view.findViewById<CircularCounter>(R.id.counterfragment_counter_view)
        view.findViewById<Button>(R.id.counterfragment_one_button).setOnClickListener {
            counter.progress = 0.05f
        }
        view.findViewById<Button>(R.id.counterfragment_five_button).setOnClickListener {
            counter.progress = 0.2f
        }
        view.findViewById<Button>(R.id.counterfragment_ten_button).setOnClickListener {
            counter.progress = 0.4f
        }
        return view
    }
}