package com.jeremygiddings.assignment3.ui.current_workout

import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.jeremygiddings.assignment3.dataClasses.CurrentExercise
import com.jeremygiddings.assignment3.dataClasses.toCompletedExerciseEntity
import com.jeremygiddings.assignment3.dataClasses.toCurrentExercise
import com.jeremygiddings.assignment3.database.ExerciseRepository
import com.jeremygiddings.assignment3.database.WorkoutApplication
import com.jeremygiddings.assignment3.database.WorkoutRepository
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentWorkoutViewModel(
        private val workoutRepository: WorkoutRepository,
        private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Current Workout"
    }
    val text: LiveData<String> = _text

    private var currentSessionId: Long? = null

    // Use MutableLiveData to hold the list of selected exercises
    private val _selectedExercises = MutableLiveData<MutableList<CurrentExercise>>(mutableListOf())
    val selectedExercises: LiveData<MutableList<CurrentExercise>> = _selectedExercises

    data class ObserverDetails(
        val repsCallback: Observable.OnPropertyChangedCallback,
        val isCompleteCallback: Observable.OnPropertyChangedCallback
    )

    private val observerMap = mutableMapOf<Long, ObserverDetails>()

    init {
        Log.d("current_workout_view_model", "init called")
        loadInProgressWorkout()
    }

    //factory to allow viewmodel to take repository arguments
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                return CurrentWorkoutViewModel(
                    (application as WorkoutApplication).workoutRepository,
                    (application as WorkoutApplication).exerciseRepository
                ) as T
            }
        }
    }

    //queries the database for a current workout session, if one exists then it will load the exercises into the list
    private fun loadInProgressWorkout() {
        viewModelScope.launch {
            // Check for an in-progress workout session


                val inProgressSession = withContext(Dispatchers.IO){ workoutRepository.getInProgressWorkout()}

                if (inProgressSession != null) {
                    Log.d("database_load_workout", "Previous workout detected, loading workout")
                    Log.d("database_load_workout", "In-progress session: $inProgressSession")
                    currentSessionId = inProgressSession.sessionId
                    // Load completed exercises for this session
                    currentSessionId?.let {
                        val completedExercises =  withContext(Dispatchers.IO) {workoutRepository.getCompletedExercisesForSession(it)}
                        val currentExerciseList = mutableListOf<CurrentExercise>()
                        withContext(Dispatchers.IO) {
                            for (completedExercise in completedExercises) {
                                val exerciseEntity =
                                    exerciseRepository.getExerciseById(completedExercise.exerciseId)
                                Log.d(
                                    "database_load_workout",
                                    "Exercise entity in  session: $completedExercise"
                                )
                                val currentExercise =
                                    completedExercise.toCurrentExercise(exerciseEntity)
                                currentExerciseList.add(currentExercise)
                                observeCurrentExerciseChanges(currentExercise)
                            }
                        }
                        _selectedExercises.postValue(currentExerciseList)
                    }


                } else {
                    // Start a new workout session
                    startNewWorkout()
                    Log.d("database_load_workout", "New workout started")
                }

        }
    }

    //start a new workout by adding a new workout session into the database
    private fun startNewWorkout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                currentSessionId = workoutRepository.startWorkoutSession()
            }
            _selectedExercises.value = mutableListOf()
            Log.d(
                "database_start_workout",
                "\"Inserted workout session with ID: $currentSessionId\""
                )

        }
    }

    //updates the workout session date and completed status in the database, clears the list of exercises and starts a new workout
    fun completeWorkout() {
        viewModelScope.launch {
            currentSessionId?.let {
                withContext(Dispatchers.IO) {
                    workoutRepository.updateWorkoutSessionDate(it)
                    workoutRepository.completeWorkoutSession(it)
                }
            }
            clearExercisesForCompletion()
            startNewWorkout()
        }
    }

    //adds a new exercise to the list of current exercises if its not already added, updates the current exercise list, attaches observers and adds current exercise to the database
    fun addExercise(exercise: ExerciseEntity): Boolean {
        val currentList = _selectedExercises.value ?: mutableListOf()
        val newCurrentExercise = CurrentExercise(exercise)
        if (!currentList.contains(newCurrentExercise)) {
            currentList.add(newCurrentExercise)
            _selectedExercises.postValue(currentList)
            observeCurrentExerciseChanges(newCurrentExercise)
            // Save to database
            saveCurrentExerciseToDatabase(newCurrentExercise)
            Log.d("database save", "Exercise added and saved to database")
            return true
        }
        return false
    }

    // observe changes in CurrentExercise and update the database
    private fun observeCurrentExerciseChanges(currentExercise: CurrentExercise) {
        val repsCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                updateExerciseInDatabase(currentExercise)
                Log.d("database_observer_reps_changed", "reps value changed")
            }
        }

        val isCompleteCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                updateExerciseInDatabase(currentExercise)
                Log.d("database_observer_isCompleted_changed", "isCompleted value changed")
            }
        }

        // attach the callbacks
        currentExercise.reps.addOnPropertyChangedCallback(repsCallback)
        currentExercise.isComplete.addOnPropertyChangedCallback(isCompleteCallback)

        // store the callbacks in the map for later removal
        observerMap[currentExercise.id] =
        ObserverDetails(repsCallback, isCompleteCallback)
    }

    // save current exercise to the database
    private fun saveCurrentExerciseToDatabase(currentExercise: CurrentExercise) {
        viewModelScope.launch {
            currentSessionId?.let { sessionId ->
                val completedExerciseEntity = currentExercise.toCompletedExerciseEntity(sessionId)

                Log.d("database_save_entity_id", "${completedExerciseEntity.id}")

                val primaryKey = withContext(Dispatchers.IO){ workoutRepository.addCompletedExercise(completedExerciseEntity)}
                currentExercise.id = primaryKey
                    Log.d("database_save_exercise", "$currentExercise")
            }
        }
    }

    // update current exercise in the database
      fun updateExerciseInDatabase(currentExercise: CurrentExercise) {
        viewModelScope.launch {
            currentSessionId?.let { sessionId ->
                val completedExerciseEntity = currentExercise.toCompletedExerciseEntity(sessionId)
                withContext(Dispatchers.IO) {
                    workoutRepository.updateCompletedExercise(completedExerciseEntity)
                }
                Log.d("database_update_new_values", "$completedExerciseEntity")
            }
        }
    }

    // remove an exercise from the list and remove from database
    fun removeExercise(currentExercise: CurrentExercise) {
        removeObservers(currentExercise)
        val currentList = _selectedExercises.value?.toMutableList()
        currentList?.remove(currentExercise)
        _selectedExercises.value = currentList!!
        // Remove from database
        removeExerciseFromDatabase(currentExercise)
    }

    //removes a current exercise from the database
    private fun removeExerciseFromDatabase(currentExercise: CurrentExercise) {
        viewModelScope.launch {
            currentSessionId?.let { sessionId ->
                val completedExerciseEntity = currentExercise.toCompletedExerciseEntity(sessionId)
                withContext(Dispatchers.IO) {
                    workoutRepository.deleteCompletedExercise(completedExerciseEntity)
                }
            }
        }
    }

    // clear the list and remove session from the database
    fun clearExercises() {
        removeAllObservers()
        _selectedExercises.value = mutableListOf()
        // Clear exercises from database
        viewModelScope.launch {
            currentSessionId?.let { sessionId ->
                workoutRepository.deleteWorkoutSession(sessionId)
                startNewWorkout()
            }
        }
    }

    // clear the list
    private fun clearExercisesForCompletion() {
        removeAllObservers()
        _selectedExercises.value = mutableListOf()
    }

    private fun removeObservers(currentExercise: CurrentExercise) {
        observerMap[currentExercise.id]?.let { observerDetails ->
            currentExercise.reps.removeOnPropertyChangedCallback(observerDetails.repsCallback)
            currentExercise.isComplete.removeOnPropertyChangedCallback(observerDetails.isCompleteCallback)
            observerMap.remove(currentExercise.id)
        }
    }

    private fun removeAllObservers() {
        _selectedExercises.value?.forEach { exercise ->
            removeObservers(exercise)
        }
    }
}