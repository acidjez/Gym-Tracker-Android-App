package com.jeremygiddings.assignment3.dataClasses

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.jeremygiddings.assignment3.database.entities.CompletedExerciseEntity
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity


data class CurrentExercise(

    val exercise: ExerciseEntity,
    val weight: ObservableField<String> = ObservableField("0"),
    val isComplete: ObservableBoolean = ObservableBoolean(false),
    val reps: ObservableInt = ObservableInt(0),
    //val reps: MutableLiveData<Int> = MutableLiveData(0),
    var id: Long = 0

) {

    fun incrementReps() {
        if (reps.get() < 99) {
            reps.set(reps.get() + 1)
        }
//        if (reps.value!! < 99) {
//            reps.value = (reps.value!!.plus(1))
//        }
    }
    fun decrementReps() {
        if (reps.get() > 0) {
            reps.set(reps.get() - 1)
        }
//        if (reps.value!! > 0) {
//            reps.value = (reps.value!!.minus(1))
//        }
    }

    //evaluates by checking the exercise id, allows for easy evaluation if exercise is already added to the list
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CurrentExercise) return false

        return exercise.id == other.exercise.id
    }

    override fun hashCode(): Int {
        var result = exercise.hashCode()
        result = 31 * result + weight.hashCode()
        result = 31 * result + isComplete.hashCode()
        result = 31 * result + reps.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}

fun CompletedExerciseEntity.toCurrentExercise(exercise: ExerciseEntity): CurrentExercise {
    return CurrentExercise(
        id = id,
        exercise = exercise,
        weight = ObservableField(this.weight),
        isComplete = ObservableBoolean(this.isComplete),
        reps = ObservableInt(this.reps)
        //reps = MutableLiveData(0)
    )
}

fun CurrentExercise.toCompletedExerciseEntity(sessionId: Long): CompletedExerciseEntity {
    return CompletedExerciseEntity(
        id = id,
        exerciseId = this.exercise.id,
        weight = this.weight.get() ?: "0",
        isComplete = this.isComplete.get(),
        reps = this.reps.get(),
        //reps = this.reps.value!!,
        sessionId = sessionId
    )
}