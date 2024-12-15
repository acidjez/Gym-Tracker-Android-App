package com.jeremygiddings.assignment3.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jeremygiddings.assignment3.CurrentWorkoutListeners
import com.jeremygiddings.assignment3.ExercisesListeners

import com.jeremygiddings.assignment3.R
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity

class ExerciseAdapter(private var data: List<ExerciseEntity>,
                      private val listeners: ExercisesListeners
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.layout_excercise, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, listeners)
    }

    fun updateData(newData: List<ExerciseEntity>) {
        data = newData
        notifyDataSetChanged()
    }
    fun updateDataForUi() {

        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val exerciseItemLayout: View = v.findViewById(R.id.exercise_item_layout)
        private val name: TextView = v.findViewById(R.id.exercise_name)
        private val addButton: Button = v.findViewById(R.id.button)
        private val image: ImageView = v.findViewById(R.id.imageView)

        fun bind(item: ExerciseEntity, listeners: ExercisesListeners) {

            if(listeners.isInCurrentWorkout(item)) {
                addButton.text = buildString {
                    append("-")
                }
            } else {
                addButton.text = buildString {
        append("+")
    }
            }
            name.text = item.name
            image.setImageResource(item.imageResId)

            addButton.setOnClickListener{
                listeners.onAddClicked(item)

            }

            exerciseItemLayout.setOnClickListener{
                listeners.onItemClicked(item)
            }
        }

    }
}