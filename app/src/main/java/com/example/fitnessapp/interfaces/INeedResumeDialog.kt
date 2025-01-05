package com.example.fitnessapp.interfaces

/** INeedResumeDialog interface to define behavior for dialogs which need to call resume method on
 * activity onResume().
 * This interface is needed as our dialogs inherit Dialog class, which unlike DialogFragment does not
 * have it's own lifecycle and cannot override onResume()
 */
interface INeedResumeDialog {
    /** Callback to execute when resume is needed */
    fun resume()
}