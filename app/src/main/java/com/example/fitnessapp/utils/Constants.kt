package com.example.fitnessapp.utils


import com.example.fitnessapp.BuildConfig

/** Object to with all constants */
object Constants {
    const val URL: String = BuildConfig.BASE_URL

    /** Enum with all response codes defined server-side */
    enum class ResponseCode {
        SUCCESS,
        FAIL,
        UNEXPECTED_ERROR,
        EXERCISE_ALREADY_EXISTS,
        TOKEN_EXPIRED,
        REFRESH_TOKEN
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
        NOTIFICATIONS
    }

    /** Enum with the position of the panel in the fragment state adapter  */
    enum class PanelIndices {
        MAIN,
        WORKOUT,
        TEMPORARY,
        ANOTHER_TEMPORARY
    }

    /** Enum with notification types */
    enum class NotificationType {
        INVITED_TO_TEAM,
        JOINED_TEAM,
        REMOVED_FROM_TEAM,
        DECLINED_TEAM_INVITATION
    }

    /** Object containing request end point values */
    object RequestEndPoints {
        private const val USER = "user"
        private const val WORKOUT = "workout"
        private const val EXERCISE = "exercise"
        private const val MUSCLE_GROUP = "muscle-group"
        private const val USER_PROFILE = "user-profile"
        private const val WORKOUT_TEMPLATE = "workout-template"
        private const val TEAM = "team"
        private const val NOTIFICATION = "notification"

        const val REGISTER = "$USER/register"
        const val LOGIN = "$USER/login"
        const val LOGOUT = "$USER/logout"
        const val CHANGE_PASSWORD = "$USER/change-password"
        const val VALIDATE_TOKEN = "$USER/validate-token"

        const val ADD_WORKOUT = "$WORKOUT/add"
        const val UPDATE_WORKOUT = "$WORKOUT/update"
        const val DELETE_WORKOUT = "$WORKOUT/delete"
        const val GET_WORKOUTS = "$WORKOUT/get-workouts"
        const val GET_WORKOUT = "$WORKOUT/get-workout"
        const val GET_WEIGHT_UNITS  = "$WORKOUT/get-weight-units"

        const val ADD_EXERCISE_TO_WORKOUT = "$EXERCISE/add-to-workout"
        const val UPDATE_EXERCISE_FROM_WORKOUT = "$EXERCISE/update-exercise-from-workout"
        const val DELETE_EXERCISE_FROM_WORKOUT = "$EXERCISE/delete-exercise-from-workout"
        const val ADD_EXERCISE = "$EXERCISE/add"
        const val UPDATE_EXERCISE = "$EXERCISE/update"
        const val DELETE_EXERCISE = "$EXERCISE/delete"
        const val COMPLETE_SET = "$EXERCISE/complete-set"
        const val GET_EXERCISES_FOR_MG = "$EXERCISE/get-by-mg-id"
        const val GET_MG_EXERCISE = "$EXERCISE/get-mg-exercise"

        const val GET_MUSCLE_GROUPS_FOR_USER = "$MUSCLE_GROUP/get-by-user"

        const val UPDATE_USER_DEFAULT_VALUES = "$USER_PROFILE/update-default-values"
        const val UPDATE_USER_PROFILE = "$USER_PROFILE/update-profile"
        const val GET_USER_DEFAULT_VALUES = "$USER_PROFILE/get-default-values"

        const val ADD_WORKOUT_TEMPLATE  = "$WORKOUT_TEMPLATE/add"
        const val DELETE_WORKOUT_TEMPLATE  = "$WORKOUT_TEMPLATE/delete"
        const val GET_WORKOUT_TEMPLATES  = "$WORKOUT_TEMPLATE/get-templates"

        const val ADD_TEAM  = "$TEAM/add"
        const val UPDATE_TEAM  = "$TEAM/update"
        const val DELETE_TEAM  = "$TEAM/delete"
        const val INVITE_MEMBER  = "$TEAM/invite-member"
        const val REMOVE_MEMBER  = "$TEAM/remove-member"
        const val ACCEPT_TEAM_INVITE  = "$TEAM/accept-invite"
        const val DECLINE_TEAM_INVITE  = "$TEAM/decline-invite"
        const val GET_MY_TEAMS  = "$TEAM/my-teams"
        const val GET_USERS_TO_INVITE  = "$TEAM/get-users-to-invite"
        const val GET_TEAM_MEMBERS  = "$TEAM/get-team-members"

        const val NOTIFICATION_REVIEWED  = "$NOTIFICATION/reviewed"
        const val DELETE_NOTIFICATION  = "$NOTIFICATION/delete"
        const val GET_NOTIFICATIONS  = "$NOTIFICATION/get-notifications"
        const val GET_JOIN_TEAM_NOTIFICATION_DETAILS  = "$NOTIFICATION/get-join-team-notification-details"
        const val REFRESH_NOTIFICATIONS = "$NOTIFICATION/refresh-notifications"
    }
}