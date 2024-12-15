package com.jeremygiddings.assignment3.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.jeremygiddings.assignment3.database.ExerciseRepository
import com.jeremygiddings.assignment3.database.WorkoutApplication
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity
import com.jeremygiddings.assignment3.ui.current_workout.CurrentWorkoutViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class HomeViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Add Exercises to your Workout"
    }
    val text: LiveData<String> = _text

    val exercises: LiveData<List<ExerciseEntity>> = exerciseRepository.getAllExercises().asLiveData()

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
                return HomeViewModel(
                    (application as WorkoutApplication).exerciseRepository
                ) as T
            }
        }
    }



    //loads the list of exercises from a JSON file. the JSON contains a version number which is saved to preferences once data is loaded.
    //exercise data is only read if the version number in the json file is greater than the one saved in preferences
    fun loadDataFromJson(context: Context) {
        try {
            val jsonString = context.assets.open("exercise_data.json").bufferedReader().readText()
            val jsonObject = JSONObject(jsonString)
            val jsonVersionNumber = jsonObject.getInt("version")
            val preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val currentVersion = preferences.getInt("current_version", 0)
            Log.d("mainviewmodel json version check", "$jsonVersionNumber")
            Log.d("mainviewmodel preferences version check", "$currentVersion")
            if (jsonVersionNumber > currentVersion) {
                val exercisesFromJson = jsonObject.getJSONArray("exercises")
                for (i in 0 until exercisesFromJson.length()) {
                    val jsonExercise = exercisesFromJson.getJSONObject(i)
                    val id = jsonExercise.getInt("id").toLong()
                    val name = jsonExercise.getString("name")
                    val instructions = jsonExercise.getString("instructions")
                    val imageResId = jsonExercise.getString("imageResId")
                    val resourceId =
                        context.resources.getIdentifier(imageResId, "drawable", context.packageName)
                    viewModelScope.launch {
                        exerciseRepository.insertExercises(
                            ExerciseEntity(
                                id,
                                name,
                                instructions,
                                resourceId
                            )
                        )
                    }
                }
                preferences.edit().putInt("current_version", jsonVersionNumber).apply()
            }
        } catch (e: JSONException) {
            Log.e("JSON Parser", "JSON exception", e)
        }
    }
}