package com.example.fitnessapp.utils


import com.example.fitnessapp.BuildConfig

/** Object to with all constants */
object Constants {
    const val URL: String = BuildConfig.BASE_URL

    /** Object with all custom HTTP status codes defined server-side */
    object CustomHttpStatusCode {
        val EXERCISE_ALREADY_EXISTS = 499
    }

    /** Enum with panel unique ids */
    enum class PanelUniqueId {
        MAIN,
        WORKOUT,
        TEMPLATES,
        SELECT_EXERCISE,
        MANAGE_EXERCISE,
        MANAGE_TEAMS,
        ADD_TEAM,
        EDIT_TEAM,
        NOTIFICATIONS,
        TEAM_DETAILS,
        ASSIGN_WORKOUT
    }

    /** Enum with the position of the panel in the fragment state adapter  */
    enum class PanelIndices {
        MAIN,
        WORKOUT,
        FIRST_TEMPORARY,
        SECOND_TEMPORARY
    }

    /** Enum with notification types */
    enum class NotificationType {
        INVITED_TO_TEAM,
        JOINED_TEAM,
        DECLINED_TEAM_INVITATION
    }

    /** Enum with team types when fetching teams */
    enum class ViewTeamAs {
        COACH,
        MEMBER
    }

    /** Object containing request end point values */
    object RequestEndPoints {
        private const val USERS = "users"

        const val USER_PROFILES = "user-profiles"
        const val WORKOUTS = "workouts"
        const val EXERCISES = "exercises"
        const val MUSCLE_GROUPS = "muscle-groups"
        const val WORKOUT_TEMPLATES = "workout-templates"
        const val TEAMS = "teams"
        const val NOTIFICATIONS = "notifications"

        const val REGISTER = "$USERS/register"
        const val LOGIN = "$USERS/login"
        const val LOGOUT = "$USERS/logout"
        const val CHANGE_PASSWORD = "$USERS/change-password"
        const val VALIDATE_TOKEN = "$USERS/validate-token"

        const val WEIGHT_UNITS  = "$WORKOUTS/weight-units"

        const val TO_WORKOUT = "$EXERCISES/to-workout"
        const val EXERCISE_FROM_WORKOUT = "$EXERCISES/exercise-from-workout"
        const val COMPLETE_SET = "$EXERCISES/complete-set"
        const val EXERCISES_FOR_MG = "$EXERCISES/by-mg-id"
        const val MG_EXERCISE = "$EXERCISES/mg-exercise"

        const val DEFAULT_VALUES = "$USER_PROFILES/default-values"

        const val LEAVE_TEAM  = "$TEAMS/leave"
        const val INVITE_MEMBER  = "$TEAMS/invite-member"
        const val REMOVE_MEMBER  = "$TEAMS/remove-member"
        const val ACCEPT_TEAM_INVITE  = "$TEAMS/accept-invite"
        const val DECLINE_TEAM_INVITE  = "$TEAMS/decline-invite"
        const val MY_TEAMS  = "$TEAMS/my-teams"
        const val MY_TEAMS_WITH_MEMBERS  = "$TEAMS/my-teams-with-members"
        const val USERS_TO_INVITE  = "$TEAMS/users-to-invite"
        const val MY_TEAM_MEMBERS  = "$TEAMS/my-team-members"
        const val JOINED_TEAM_MEMBERS  = "$TEAMS/joined-team-members"

        const val JOIN_TEAM_NOTIFICATION_DETAILS  = "$NOTIFICATIONS/join-team-notification-details"
        const val REFRESH_NOTIFICATIONS = "$NOTIFICATIONS/refresh-notifications"
    }
}