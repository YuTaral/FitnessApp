package com.example.fitnessapp.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R

/** Custom view, used to allow the user selection between 2 text views and execute actions on selection */
class CustomSwitchView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
        ConstraintLayout(ctx, attrs, defStyleAttr) {

    /** Track the selected value */
    enum class Selected {
        LEFT,
        RIGHT
    }

    private var selected: Selected = Selected.LEFT
    private val leftText: TextView
    private val rightText: TextView
    private val fadeDuration = 500L

    init {
        // Inflate layout
        inflate(context, R.layout.view_selector_view, this)

        // Find TextViews
        leftText = findViewById(R.id.leftView)
        rightText = findViewById(R.id.rightView)

        // Set the indicator
        setSelectedIndicator()

        // Add the default click listeners
        setLeftClickListener { setSelectedIndicator() }
        setRightClickListener { setSelectedIndicator() }

        // Apply custom attributes if any
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomSwitchView)

            val text1ResId = typedArray.getResourceId(R.styleable.CustomSwitchView_leftText, 0)
            val text2ResId = typedArray.getResourceId(R.styleable.CustomSwitchView_rightText, 0)

            if (text1ResId == 0 || text2ResId == 0) {
                throw IllegalArgumentException("The 'leftText and rightText' attributes of CustomSwitchView are required and must be provided.")
            }

            leftText.text = context.getString(text1ResId)
            rightText.text = context.getString(text2ResId)

            typedArray.recycle()
        }
    }

    /** Set the text appearance based on the selected text */
    private fun setSelectedIndicator() {
        // Adjust the duration as needed (milliseconds)

        if (selected == Selected.LEFT) {
            changeTextView(rightText)
            changeTextView(leftText, R.drawable.background_select_view_left)
        } else {
            changeTextView(leftText)
            changeTextView(rightText, R.drawable.background_select_view_right)
        }
    }

    /** Set the text view to selected / not selected
     * @param textView the view to set as selected
     * @param backgroundId 0 if the textView must be set as not selected,
     * otherwise the background id to set
     */
    private fun changeTextView(textView: TextView, backgroundId: Int = 0) {
        if (backgroundId > 0) {
            // Change the background by animation
            textView.setBackgroundResource(backgroundId)
            textView.background.alpha = 0
            ObjectAnimator.ofInt(textView.background, "alpha", 0, 255).apply {
                duration = fadeDuration
                start()
            }

            // Set the text to bold
            textView.typeface = Typeface.create(textView.typeface, Typeface.BOLD)

        } else {
            // Remove the background by animation
            val currentBackground = textView.background
            ObjectAnimator.ofInt(currentBackground, "alpha", 255, 0).apply {
                duration = fadeDuration
                start()
            }
            textView.background = null

            // Remove the bold
            textView.typeface = Typeface.create(textView.typeface, Typeface.NORMAL)
        }
    }

    /** Add click listener to the left text */
    fun setLeftClickListener(callback: () -> Unit) {
        leftText.setOnClickListener {
            if (selected != Selected.LEFT) {
                selected = Selected.LEFT
                setSelectedIndicator()
                callback()
            }
        }
    }

    /** Add click listener to the right text */
    fun setRightClickListener(callback: () -> Unit) {
        rightText.setOnClickListener {
            if (selected != Selected.RIGHT) {
                selected = Selected.RIGHT
                setSelectedIndicator()
                callback()
            }
        }
    }

    /** Return the selected text as LEFT / RIGHT */
    fun getSelected(): Selected {
        return selected
    }
}