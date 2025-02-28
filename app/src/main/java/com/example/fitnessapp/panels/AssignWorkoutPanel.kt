package com.example.fitnessapp.panels

import android.animation.LayoutTransition
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.TeamMembersRecAdapter
import com.example.fitnessapp.adapters.TeamsRecAdapter
import com.example.fitnessapp.adapters.WorkoutsRecAdapter
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.QAssignWorkoutModel
import com.example.fitnessapp.models.TeamMemberModel
import com.example.fitnessapp.models.TeamWithMembersModel
import com.example.fitnessapp.network.repositories.TeamRepository
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** panel assign workout to implement the logic to assign workouts to team members */
class AssignWorkoutPanel: BasePanel(), ITemporaryPanel {
    override var id: Long = Constants.PanelUniqueId.ASSIGN_WORKOUT.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_assign_workout
    override var iconId = R.drawable.icon_tab_assign_workout
    override var panelIndex: Int = Constants.PanelIndices.FIRST_TEMPORARY.ordinal
    override var titleId: Int = R.string.assign_workout_lbl
    override val removePreviousTemporary = true

    private lateinit var title: TextView
    private lateinit var selectTeamContainer: ConstraintLayout
    private lateinit var selectMembersContainer: ConstraintLayout
    private lateinit var selectWorkoutContainer: ConstraintLayout
    private lateinit var noJoinedMembersLbl: TextView
    private lateinit var search: EditText
    private lateinit var teamsRecycler: RecyclerView
    private lateinit var membersRecycler: RecyclerView
    private lateinit var workoutsRecycler: RecyclerView
    private lateinit var selectTeamBtn: Button
    private lateinit var selectWorkoutBtn: Button
    private lateinit var selectMembersBtn: Button

    /** Dialog state - the current step when assigning workout */
    private enum class DialogState {
        SELECT_TEAM,
        SELECT_MEMBERS,
        SELECT_WORKOUT
    }

    private var _state = DialogState.SELECT_TEAM
    private var state: DialogState
        get() = _state
        set(value) {
            _state = value
            updateViews()
        }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var debounceJob: Job? = null
    private var filterWaitTimeMillis = 500L
    private var selectedTeam: TeamWithMembersModel? = null

    override fun findViews() {
        title = panel.findViewById(R.id.panel_title)
        selectTeamContainer = panel.findViewById(R.id.select_team_container)
        selectMembersContainer = panel.findViewById(R.id.select_members_container)
        selectWorkoutContainer = panel.findViewById(R.id.select_workout_container)
        search = panel.findViewById<TextInputLayout>(R.id.search).editText!!
        noJoinedMembersLbl = panel.findViewById(R.id.no_joined_members_lbl)
        teamsRecycler = panel.findViewById(R.id.teams_recycler)
        membersRecycler = panel.findViewById(R.id.members_recycler)
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)
        selectTeamBtn = panel.findViewById(R.id.select_team_btn)
        selectWorkoutBtn = panel.findViewById(R.id.select_workout_btn)
        selectMembersBtn = panel.findViewById(R.id.select_members_btn)
    }

    override fun populatePanel() {
        TeamRepository().getMyTeamsWithMembers(onSuccess = { teams ->
            if (teams.isEmpty()) {
                teamsRecycler.visibility = View.GONE
                noJoinedMembersLbl.visibility = View.VISIBLE

            } else {
                noJoinedMembersLbl.visibility = View.GONE
                teamsRecycler.visibility = View.VISIBLE

                teamsRecycler.adapter = TeamsRecAdapter(teams, callback = { team ->
                    onTeamClick(team as TeamWithMembersModel)
                })
            }
        })
    }

    override fun addClickListeners() {
        // Add team search functionality
        addTeamSearch()

        selectTeamBtn.setOnClickListener {
            onSelectTeamBtnClick()
        }

        selectWorkoutBtn.setOnClickListener {
            populateTemplates()
        }

        selectMembersBtn.setOnClickListener {
            state = DialogState.SELECT_MEMBERS
        }

        val root = panel.findViewById<ConstraintLayout>(R.id.root_view)
        root.layoutTransition = LayoutTransition()
        root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    /** Populate the team members
     * @param members the team members
     */
    private fun populateTeamMembers(members: List<TeamMemberModel>) {
        membersRecycler.adapter = TeamMembersRecAdapter(members, TeamMembersRecAdapter.AdapterType.ASSIGN_WORKOUT, callback = { member ->
            getMembersAdapter().selectUnselectForAssign(member)

            // Enable / disable select workout button
            selectWorkoutBtn.isEnabled = getMembersAdapter().getAssignWorkoutMembers().isNotEmpty()
        })
    }

    /** Populate the templates */
    private fun populateTemplates() {
        // Validate
        if (getMembersAdapter().getAssignWorkoutMembers().isEmpty()) {
            Utils.showMessageWithVibration(R.string.select_members_error_lbl)
            return
        }

        WorkoutTemplateRepository().getWorkoutTemplates(onSuccess = { templates ->
            state = DialogState.SELECT_WORKOUT

            workoutsRecycler.adapter = WorkoutsRecAdapter(templates, onClick = { workout ->
                val members = getMembersAdapter().getAssignWorkoutMembers()
                var membersText = requireContext().getString(R.string.selected_members_lbl)
                membersText += members.joinToString(separator = "\n") { it.fullName }
                val model = QAssignWorkoutModel(workout.name, membersText)
                val dialog = AskQuestionDialog(requireContext(), AskQuestionDialog.Question.ASSIGN_WORKOUT, model)

                dialog.setConfirmButtonCallback {
                    // TODO: Send request to assign the workout to the members
                }

                dialog.show()
            })
        })
    }

    /** Callback to execute on team view click - fetch the team members and update the views
     * @param team the clicked team
     */
    private fun onTeamClick(team: TeamWithMembersModel) {
        selectedTeam = team
        state = DialogState.SELECT_MEMBERS
        populateTeamMembers(selectedTeam!!.members)
        selectWorkoutBtn.isEnabled = getMembersAdapter().getAssignWorkoutMembers().isNotEmpty()
    }

    /** Update the views based on the current dialog state */
    private fun updateViews() {
        var titleId = 0
        search.setText("")

        when (state) {
            DialogState.SELECT_TEAM -> {
                selectMembersContainer.visibility = View.GONE
                selectWorkoutContainer.visibility = View.GONE

                selectTeamContainer.visibility = View.VISIBLE
                titleId = R.string.select_team_lbl
            }

            DialogState.SELECT_MEMBERS -> {
                selectTeamContainer.visibility = View.GONE
                selectWorkoutContainer.visibility = View.GONE

                selectMembersContainer.visibility = View.VISIBLE
                titleId = R.string.select_members_lbl
            }

            DialogState.SELECT_WORKOUT -> {
                selectTeamContainer.visibility = View.GONE
                selectMembersContainer.visibility = View.GONE

                selectWorkoutContainer.visibility = View.VISIBLE
                titleId = R.string.select_workout_lbl
            }
        }

        title.setText(titleId)
    }

    /** Add search functionality to search for teams / members / workouts */
    private fun addTeamSearch() {
        search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            // Use debouncing mechanism to avoid filtering the data on each letter to reduce
            // unnecessary computation
            override fun afterTextChanged(s: Editable?) {

                debounceJob?.cancel() // Cancel any ongoing debounce job

                debounceJob = coroutineScope.launch {
                    delay(filterWaitTimeMillis)

                    // Filter the displayed data
                    when (state) {
                        DialogState.SELECT_TEAM -> {
                            getTeamsAdapter().filter(s.toString().lowercase())
                        }
                        DialogState.SELECT_MEMBERS -> {
                            getMembersAdapter().filter(s.toString().lowercase())
                        }
                       DialogState.SELECT_WORKOUT -> {
                            getWorkoutAdapter().filter(s.toString().lowercase())
                        }
                    }
                }
            }
        })
    }

    /** Execute the callback when Select Team button click occurs */
    private fun onSelectTeamBtnClick() {
        getMembersAdapter().unSelectAll()
        state = DialogState.SELECT_TEAM
        getTeamsAdapter().changeSelectedTeam(selectedTeam!!.id)
        selectedTeam = null
    }

    /** Return the teams recycler adapter as TeamsRecAdapter */
    private fun getTeamsAdapter(): TeamsRecAdapter {
        return teamsRecycler.adapter as TeamsRecAdapter
    }

    /** Return the members recycler adapter as TeamsRecAdapter */
    private fun getMembersAdapter(): TeamMembersRecAdapter {
        return membersRecycler.adapter as TeamMembersRecAdapter
    }

    /** Return the workout recycler adapter as WorkoutsRecAdapter */
    private fun getWorkoutAdapter(): WorkoutsRecAdapter {
        return workoutsRecycler.adapter as WorkoutsRecAdapter
    }
}