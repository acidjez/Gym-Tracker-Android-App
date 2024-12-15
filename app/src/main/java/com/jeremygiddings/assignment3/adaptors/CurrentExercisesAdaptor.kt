package com.jeremygiddings.assignment3.adaptors

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jeremygiddings.assignment3.dataClasses.CurrentExercise
import com.jeremygiddings.assignment3.CurrentWorkoutListeners
import com.jeremygiddings.assignment3.R
import com.jeremygiddings.assignment3.databinding.LayoutCurrentExcerciseBinding

class CurrentExerciseAdapter(
    private val listeners: CurrentWorkoutListeners
) : ListAdapter<CurrentExercise, CurrentExerciseAdapter.ViewHolder>(CurrentExerciseDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: LayoutCurrentExcerciseBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_current_excercise,
            parent,
            false
        )
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listeners)
    }


    inner class ViewHolder(private val v: LayoutCurrentExcerciseBinding) : RecyclerView.ViewHolder(v.root) {
        private var currentTextWatcher: TextWatcher? = null
        fun bind(
            item: CurrentExercise,
            listeners: CurrentWorkoutListeners
        ){

            v.buttonRepsPlus.setOnClickListener {
                item.incrementReps()
            }

            v.buttonRepsMinus.setOnClickListener {
                item.decrementReps()
            }

            v.currentExerciseItemLayout.setOnLongClickListener {
                listeners.onExerciseLongClicked(item)
                true
            }
            currentTextWatcher?.let {
                v.editTextWeight.removeTextChangedListener(it)
            }
            v.editTextWeight.setText(item.weight.get())

            currentTextWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val newValue = s.toString()
                    if (newValue != item.weight.get()) {
                        item.weight.set(newValue)
                        listeners.updateDatabase(item)
                        Log.d("listener_weight", "Weight value changed")
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {   }
            }
            v.editTextWeight.addTextChangedListener(currentTextWatcher)
            //assigning databinding values
            v.currentExercise = item
            v.currentExerciseListener = listeners
            v.executePendingBindings()

        }

    }
}
class CurrentExerciseDiffCallback : DiffUtil.ItemCallback<CurrentExercise>() {
    override fun areItemsTheSame(oldItem: CurrentExercise, newItem: CurrentExercise): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CurrentExercise, newItem: CurrentExercise): Boolean {
        return oldItem == newItem &&
                oldItem.reps.get() == newItem.reps.get() &&
                //oldItem.reps.value == newItem.reps.value &&
                oldItem.weight.get() == newItem.weight.get() &&
                oldItem.isComplete.get() == newItem.isComplete.get()
    }
}