package com.jeremygiddings.assignment3.ui.current_workout


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jeremygiddings.assignment3.CurrentWorkoutListeners
import com.jeremygiddings.assignment3.adaptors.CurrentExerciseAdapter
import com.jeremygiddings.assignment3.dataClasses.CurrentExercise
import com.jeremygiddings.assignment3.databinding.FragmentCurrentWorkoutBinding
import kotlinx.coroutines.delay

class CurrentWorkoutFragment : Fragment(), CurrentWorkoutListeners {

    private var _binding: FragmentCurrentWorkoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CurrentExerciseAdapter
    private lateinit var currentWorkoutViewModel: CurrentWorkoutViewModel
    private lateinit var currentWorkoutRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentWorkoutBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //initialise the current workout viewmodel. viewmodel is shared between fragments that share an activity
        currentWorkoutViewModel = ViewModelProvider(
            requireActivity(),
            CurrentWorkoutViewModel.Factory
        )[CurrentWorkoutViewModel::class.java]

        //observe the heading textview
        val textView: TextView = binding.textWorkout
        currentWorkoutViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //set up and initialise recyclerview
        currentWorkoutRecyclerView = binding.recyclerCurrentWorkout
        linearLayoutManager = LinearLayoutManager(context)
        currentWorkoutRecyclerView.layoutManager = linearLayoutManager
        adapter = CurrentExerciseAdapter(this)

        currentWorkoutRecyclerView.adapter = adapter
        currentWorkoutRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        //listener on clear button, calls current workout view model to clear all exercises in list
        binding.buttonClearWorkout.setOnClickListener {
            currentWorkoutViewModel.clearExercises()
        }

        //listener on finished button. calls current workout viewmodel to complete the workout
        binding.buttonFinishWorkout.setOnClickListener{
            currentWorkoutViewModel.completeWorkout()
            val message = "Workout Completed. Well Done!"
            view?.let {
                Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
            }
        }

        //observe the list of current exercises, update the recycler views data if changed, also enable or disable buttons depending on if list is populated
        currentWorkoutViewModel.selectedExercises.observe(viewLifecycleOwner) { currentExercises ->
            currentExercises?.let {
                adapter.submitList(it)
                Log.d("CurrentWorkoutFragment", "Observed currentExercises update: size = ${currentExercises.size}")

                if(currentWorkoutViewModel.selectedExercises.value?.size!! > 0) {
                    binding.buttonClearWorkout.isClickable = true
                    binding.buttonClearWorkout.alpha = 1.0f
                    binding.buttonFinishWorkout.isClickable = true
                    binding.buttonFinishWorkout.alpha = 1.0f

                } else {
                    binding.buttonClearWorkout.isClickable = false
                    binding.buttonClearWorkout.alpha = 0.5f
                    binding.buttonFinishWorkout.isClickable = false
                    binding.buttonFinishWorkout.alpha = 0.5f
                }
            }
        }

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("current_workout_fragment", "view destroyed")
    }

    //implement interface click listener for when exercise is completed
    override fun onCompletedClicked(currentExercise: CurrentExercise) {
        val currentValue = currentExercise.isComplete.get()
        currentExercise.isComplete.set(!currentValue)
    }

    //implement interface click listener for when current exercise is long clicked. calls current workout viewmodel to delete the long clicked exercise
    override fun onExerciseLongClicked(currentExercise: CurrentExercise): Boolean {
        // Show a confirmation dialog before removing
        showRemoveExerciseDialog(currentExercise)
        return true
    }

    override fun updateDatabase(currentExercise: CurrentExercise) {
        currentWorkoutViewModel.updateExerciseInDatabase(currentExercise)

    }


    //displays dialog to user to confirm the deletion of a current exercise
    private fun showRemoveExerciseDialog(currentExercise: CurrentExercise) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Exercise")
            .setMessage("Are you sure you want to remove this exercise?")
            .setPositiveButton("Yes") { _, _ ->
                currentWorkoutViewModel.removeExercise(currentExercise)
            }
            .setNegativeButton("No", null)
            .show()
    }
}