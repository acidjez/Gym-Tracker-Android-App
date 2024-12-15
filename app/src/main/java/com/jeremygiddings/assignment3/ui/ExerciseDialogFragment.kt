package com.jeremygiddings.assignment3.ui

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity
import com.jeremygiddings.assignment3.databinding.FragmentExerciseDialogBinding

class ExerciseDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentExerciseDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var exercise: ExerciseEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            exercise = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable("exercise", ExerciseEntity::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable("exercise")!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentExerciseDialogBinding.inflate(inflater, container, false)
        val exerciseNameTextView: TextView = binding.textViewExerciseName
        val exerciseInstructionsTextView: TextView = binding.textViewExerciseInstructions
        val exerciseImageView: ImageView = binding.imageViewExerciseImage

        exerciseNameTextView.text = exercise.name
        exerciseInstructionsTextView.text = exercise.instructions
        exerciseImageView.setImageResource(exercise.imageResId)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(exercise: ExerciseEntity): ExerciseDialogFragment =
            ExerciseDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("exercise", exercise)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}