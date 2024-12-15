package com.jeremygiddings.assignment3.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremygiddings.assignment3.adaptors.WorkoutHistoryAdaptor
import com.jeremygiddings.assignment3.database.WorkoutApplication
import com.jeremygiddings.assignment3.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: WorkoutHistoryAdaptor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        historyViewModel = ViewModelProvider(
            this,
            HistoryViewModel.Factory
        )[HistoryViewModel::class]

        val textView: TextView = binding.textHistory
        historyViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //set up and initialise recyclerview
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = WorkoutHistoryAdaptor(emptyList())
        recyclerView.adapter = adapter

        //observe list of completed workouts and update adapter data on change
        historyViewModel.completedWorkouts.observe(viewLifecycleOwner) { completedWorkouts ->
            adapter.updateData(completedWorkouts)
            Log.d("history_fragment", "$completedWorkouts")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}