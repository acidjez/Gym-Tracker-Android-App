package com.jeremygiddings.assignment3.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeremygiddings.assignment3.R
import com.jeremygiddings.assignment3.dataClasses.CompletedWorkout

class WorkoutHistoryAdaptor(private var data: List<CompletedWorkout>): RecyclerView.Adapter<WorkoutHistoryAdaptor.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.layout_workout_history, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    fun updateData(newData: List<CompletedWorkout>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: CompletedWorkout) {
            // Bind workout session details
            val workoutDateTextView = view.findViewById<TextView>(R.id.textView_date)
            workoutDateTextView.text = buildString {
        append("Session on ")
        append(item.session.date)
    }

            // Set up RecyclerView for the completed exercises within this session
            val workoutExercisesRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_workout_exercises)
            workoutExercisesRecyclerView.layoutManager = LinearLayoutManager(view.context)
            workoutExercisesRecyclerView.adapter = CompletedWorkoutExercisesAdaptor(item.completedExercises)
        }
    }
}