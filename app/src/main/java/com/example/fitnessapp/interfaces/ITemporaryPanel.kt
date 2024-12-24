package com.example.fitnessapp.interfaces

/** Interface to define common logic for all temporary panels */
interface ITemporaryPanel {
    /** Indicates whether the current temporary panel can be displayed after another temporary panel
     * e.g displaying Add Team panel after Teams panel
     */
    val removePreviousTemporary: Boolean
}