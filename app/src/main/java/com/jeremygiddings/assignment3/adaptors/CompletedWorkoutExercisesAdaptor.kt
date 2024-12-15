package com.jeremygiddings.assignment3.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jeremygiddings.assignment3.R
import com.jeremygiddings.assignment3.database.views.CompletedExerciseWithName

class CompletedWorkoutExercisesAdaptor(private var data: List<CompletedExerciseWithName>): RecyclerView.Adapter<CompletedWorkoutExercisesAdaptor.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_exercise_workout_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = data[position]
        holder.bind(exercise)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: CompletedExerciseWithName) {
            val exerciseNameTextView = view.findViewById<TextView>(R.id.textView_exercise_name)
            val repsTextView = view.findViewById<TextView>(R.id.textView_reps_value)
            val weightTextView = view.findViewById<TextView>(R.id.textView_weight_value)
            val layout = view.findViewById<ConstraintLayout>(R.id.layout_exercise_workout_history)
            //set views to exercise data
            if(item.isComplete) {
                layout.setBackgroundResource(R.drawable.rounded_green_background)
            } else {
                layout.setBackgroundResource(R.drawable.rounded_red_background)
            }
            exerciseNameTextView.text = item.name
            repsTextView.text = item.reps.toString()
            weightTextView.text = item.weight
        }
    }
}