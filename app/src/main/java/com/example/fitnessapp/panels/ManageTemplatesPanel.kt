package com.example.fitnessapp.panels

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.adapters.WorkoutsRecAdapter
import com.example.fitnessapp.dialogs.AddEditTemplateDialog
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.dialogs.DateTimePickerDialog
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/** Templates Panel class to implement the logic for managing templates */
class ManageTemplatesPanel: BasePanel(), ITemporaryPanel {
    override var id: Long = Constants.PanelUniqueId.TEMPLATES.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_templates
    override var panelIndex: Int = Constants.PanelIndices.TEMPORARY.ordinal
    override var titleId: Int = R.string.manage_templates_lbl
    override var iconId = R.drawable.icon_tab_manage_templates
    override val removePreviousTemporary = true

    private lateinit var actionSpinner: Spinner
    private lateinit var search: EditText
    private lateinit var noTemplatesLbl: TextView
    private lateinit var fromDateContainer: ConstraintLayout
    private lateinit var fromDateLbl: TextView
    private lateinit var templatesRecycler: RecyclerView
    private lateinit var closeBtn: Button

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var debounceJob: Job? = null
    private var filterWaitTimeMillis = 500L
    private var isSpinnerInitialized = false
    private lateinit var startDate: Date
    private var _selectedAction = SpinnerActions.START_WORKOUT
    private var selectedAction: SpinnerActions
        get() = _selectedAction
        set(value) {
            _selectedAction = value
            updateViews()
        }


    /** Spinner action types */
    private enum class SpinnerActions(private val stringId: Int) {
        START_WORKOUT(R.string.action_start_workout_from_template),
        ADD_TEMPLATE(R.string.action_add_template),
        EDIT_TEMPLATE(R.string.action_update_template),
        DELETE_TEMPLATE(R.string.action_delete_template);

        override fun toString(): String {
            return Utils.getActivity().getString(stringId)
        }
    }

    override fun findViews() {
        actionSpinner = panel.findViewById(R.id.template_list_action_spinner)
        fromDateContainer = panel.findViewById(R.id.workouts_from_date_container)
        search = panel.findViewById<TextInputLayout>(R.id.search).editText!!
        fromDateLbl = panel.findViewById(R.id.workout_filter_lbl)
        noTemplatesLbl = panel.findViewById(R.id.no_templates_lbl)
        templatesRecycler = panel.findViewById(R.id.templates_recycler)
        closeBtn = panel.findViewById(R.id.cancel_btn)
    }

    override fun populatePanel() {
        // Disable the state restoration in case the templates panel is recreated
        search.isSaveEnabled = false
        actionSpinner.isSaveEnabled = false

        // Create the adapter
        actionSpinner.adapter = CustomSpinnerAdapter(panel.context, false,
                                                        SpinnerActions.entries.map { it.toString() })

        // Default start date for workouts fetch to -1 month backwards
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        startDate = calendar.time

        fromDateLbl.text = String.format(Utils.getActivity().getString(R.string.workouts_filter_lbl), Utils.defaultFormatDate(startDate))

        // Pre-select 'Start workout'
        isSpinnerInitialized = false
        setDefaultAction()

        populateTemplates(false)
    }

    override fun addClickListeners() {
        // Add search functionality
        addSearch()

        fromDateContainer.setOnClickListener { selectDate() }

        closeBtn.setOnClickListener { Utils.getPanelAdapter().refreshWorkoutPanel(null) }

        // Fetch different exercises when item changes
        actionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isSpinnerInitialized) {
                    // Ignore the initial call which is triggered when the spinner is initialized
                    // with the adapter
                    isSpinnerInitialized = true
                    return
                }

                // Store the selected action
                selectedAction = SpinnerActions.entries[position]

                // Populate the data
                if (selectedAction == SpinnerActions.ADD_TEMPLATE) {
                    populateWorkouts()
                } else {
                    populateTemplates(false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /** Add search functionality to search for templates / workouts */
    private fun addSearch() {
        search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            // Use debouncing mechanism to avoid filtering the data on each letter to reduce
            // unnecessary computation
            override fun afterTextChanged(s: Editable?) {
                if (!::templatesRecycler.isInitialized || templatesRecycler.adapter == null) {
                    return
                }

                debounceJob?.cancel() // Cancel any ongoing debounce job

                debounceJob = coroutineScope.launch {
                    delay(filterWaitTimeMillis)
                    (templatesRecycler.adapter as WorkoutsRecAdapter).filter(s.toString().lowercase())
                }
            }
        })
    }

    /** Select the workouts start date */
    private fun selectDate() {
        val dialog = DateTimePickerDialog(requireContext())

        dialog.setOnSaveCallback { date ->
            dialog.dismiss()
            startDate = date
            fromDateLbl.text = String.format(Utils.getActivity().getString(R.string.workouts_filter_lbl), Utils.defaultFormatDate(startDate))

            populateWorkouts()
        }

        dialog.show()
    }

    /** Update the visible views on the when action selection change occurs */
    private fun updateViews() {
        search.setText("")
        noTemplatesLbl.visibility = View.GONE

        if (selectedAction == SpinnerActions.ADD_TEMPLATE) {
            fromDateContainer.visibility = View.VISIBLE
        } else {
            fromDateContainer.visibility = View.GONE
        }
    }

    /** Fetch the workouts in the given time period and populate the panel */
    private fun populateWorkouts() {
        WorkoutRepository().getWorkouts(startDate, onSuccess = { serializedWorkouts ->
            if (serializedWorkouts.isEmpty()) {
                templatesRecycler.visibility = View.GONE
                noTemplatesLbl.visibility = View.VISIBLE
                noTemplatesLbl.text = requireContext().getString(R.string.no_workouts)

            } else {
                noTemplatesLbl.visibility = View.GONE
                templatesRecycler.visibility = View.VISIBLE
                setAdapter(serializedWorkouts.map { WorkoutModel(it) }.toMutableList())
            }
        })
    }

    /** Set the data in the recycler adapter
     * @param data the workouts / templates
     */
    private fun setAdapter(data: MutableList<WorkoutModel>) {
        templatesRecycler.adapter = WorkoutsRecAdapter(data, onClick = { selected ->
            when (selectedAction) {
                SpinnerActions.START_WORKOUT -> {
                    AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.ADD, selected).show()
                }
                SpinnerActions.DELETE_TEMPLATE -> {
                    val dialog = AskQuestionDialog(requireContext(), AskQuestionDialog.Question.DELETE_TEMPLATE, selected)

                    dialog.setConfirmButtonCallback(callback = {
                        WorkoutTemplateRepository().deleteWorkoutTemplate(selected.id, onSuccess = {
                            dialog.dismiss()
                            populateTemplates(false)
                        })
                    })

                    dialog.show()
                }
                SpinnerActions.ADD_TEMPLATE -> {
                    AddEditTemplateDialog(requireContext(), selected, AddEditTemplateDialog.Mode.ADD).show()
                }
                SpinnerActions.EDIT_TEMPLATE -> {
                    AddEditTemplateDialog(requireContext(), selected, AddEditTemplateDialog.Mode.UPDATE).show()
                }
            }
        })
    }

    /** Set the spinner to the default value */
    private fun setDefaultAction() {
        selectedAction = SpinnerActions.START_WORKOUT
        actionSpinner.setSelection(selectedAction.ordinal)
    }

    /** Populate templates recycler
     * @param resetAction whether to set the selectionAction to the START_WORKOUT (default one)
     */
     fun populateTemplates(resetAction: Boolean) {
         if (resetAction) {
             // populateTemplates may be called from AddEditTemplateDialog,
             // make sure the correct selectedAction value is set
             setDefaultAction()
         }

        // Get the templates and populate the recycler
        WorkoutTemplateRepository().getWorkoutTemplates(onSuccess = { serializedTemplates ->
            if (serializedTemplates.isEmpty()) {
                templatesRecycler.visibility = View.GONE
                noTemplatesLbl.visibility = View.VISIBLE
                noTemplatesLbl.text = requireContext().getString(R.string.no_templates)

            } else {
                noTemplatesLbl.visibility = View.GONE
                templatesRecycler.visibility = View.VISIBLE
                setAdapter(serializedTemplates.map { WorkoutModel(it) }.toMutableList())
            }
        })
    }
}