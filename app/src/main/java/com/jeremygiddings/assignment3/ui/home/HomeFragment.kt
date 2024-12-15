package com.jeremygiddings.assignment3.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jeremygiddings.assignment3.ExercisesListeners
import com.jeremygiddings.assignment3.R
import com.jeremygiddings.assignment3.adaptors.ExerciseAdapter
import com.jeremygiddings.assignment3.dataClasses.CurrentExercise
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity
import com.jeremygiddings.assignment3.databinding.FragmentHomeBinding
import com.jeremygiddings.assignment3.ui.ExerciseDialogFragment
import com.jeremygiddings.assignment3.ui.current_workout.CurrentWorkoutViewModel

class HomeFragment : Fragment(), ExercisesListeners {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter : ExerciseAdapter
    private lateinit var currentWorkoutViewModel: CurrentWorkoutViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //initialise the exercise viewmodel, local to this fragment
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModel.Factory
        )[HomeViewModel::class]

        //initialise the current workout viewmodel. viewmodel is shared between fragments that share an activity
        currentWorkoutViewModel = ViewModelProvider(
            requireActivity(),
            CurrentWorkoutViewModel.Factory
        )[CurrentWorkoutViewModel::class.java]

        //observe the heading textview
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //set up and initialise recyclerview
        val exerciseRecyclerView: RecyclerView = binding.recyclerExercises

        linearLayoutManager = LinearLayoutManager(context)
        exerciseRecyclerView.layoutManager = linearLayoutManager

        adapter = ExerciseAdapter(emptyList(), this)
        exerciseRecyclerView.adapter = adapter
        exerciseRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        //observe changes to list of exercises and update recyclerview data. ie when app is opened and data pulled from database
        homeViewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            adapter.updateData(exercises)
        }

        currentWorkoutViewModel.selectedExercises.observe(viewLifecycleOwner) {
            adapter.updateDataForUi()
        }
        return root
    }

    //call home viewmodel to import data from JSON file
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeViewModel.loadDataFromJson(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //implement interface click listener for when exercise is added. adds exercise to currentworkout viewmodel
    override fun onAddClicked(exercise: ExerciseEntity) {

        val isInWorkout = isInCurrentWorkout(exercise)
        if (isInWorkout) {
            currentWorkoutViewModel.removeExercise(CurrentExercise(exercise))
        } else {
            currentWorkoutViewModel.addExercise(exercise)
        }

        val message: String = if(!isInWorkout) "${exercise.name} added to your workout" else "${exercise.name} Removed from your workout"
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }

    //implement interface click listener for when an exercise is clicked.  Shows a bottom sheet dialog fragment with more information
    override fun onItemClicked(exercise: ExerciseEntity) {

        val exerciseDetailModal = ExerciseDialogFragment.newInstance(exercise)
        exerciseDetailModal.show(parentFragmentManager, "ExerciseDetailModal")
    }

    override fun isInCurrentWorkout(exercise: ExerciseEntity) : Boolean {
        return currentWorkoutViewModel.selectedExercises.value?.contains(CurrentExercise(exercise))!!

    }
}