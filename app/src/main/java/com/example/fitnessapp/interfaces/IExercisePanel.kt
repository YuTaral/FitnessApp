package com.example.fitnessapp.interfaces

import com.example.fitnessapp.models.MGExerciseModel

/** This interface is used to define populateExercises method, which is going to be implemented
 *  by the panels temporary exercise panels, in order to re-populate the exercises when panel update is
 *  necessary
 */
interface IExercisePanel {
    fun populateExercises(exercises: List<MGExerciseModel>, initializeAdapter: Boolean)
}