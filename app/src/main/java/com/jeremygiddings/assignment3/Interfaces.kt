package com.jeremygiddings.assignment3

import com.jeremygiddings.assignment3.dataClasses.CurrentExercise
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity

interface CurrentWorkoutListeners {
    fun onCompletedClicked(currentExercise: CurrentExercise)
    fun onExerciseLongClicked(currentExercise: CurrentExercise): Boolean
    fun updateDatabase(currentExercise: CurrentExercise)

}

interface ExercisesListeners {
    fun onAddClicked(exercise: ExerciseEntity)
    fun onItemClicked(exercise: ExerciseEntity)
    fun isInCurrentWorkout(exercise: ExerciseEntity) : Boolean
}
