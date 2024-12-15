package com.jeremygiddings.assignment3.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.CreationExtras
import com.jeremygiddings.assignment3.dataClasses.CompletedWorkout
import com.jeremygiddings.assignment3.database.WorkoutApplication
import com.jeremygiddings.assignment3.database.WorkoutRepository
import com.jeremygiddings.assignment3.ui.home.HomeViewModel

class HistoryViewModel(workoutRepository: WorkoutRepository) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Workout History"
    }
    val text: LiveData<String> = _text

    val completedWorkouts: LiveData<List<CompletedWorkout>> = workoutRepository.getCompletedWorkouts().asLiveData()

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
                return HistoryViewModel(
                    (application as WorkoutApplication).workoutRepository
                ) as T
            }
        }
    }
}