package com.orbboard.boardoar.nfre.data.shar

import android.content.Context
import androidx.core.content.edit

class OrbBoardSharedPreference(context: Context) {
    private val orbBoardPrefs = context.getSharedPreferences("orbBoardSharedPrefsAb", Context.MODE_PRIVATE)

    var orbBoardSavedUrl: String
        get() = orbBoardPrefs.getString(ORB_BOARD_SAVED_URL, "") ?: ""
        set(value) = orbBoardPrefs.edit { putString(ORB_BOARD_SAVED_URL, value) }

    var orbBoardExpired : Long
        get() = orbBoardPrefs.getLong(ORB_BOARD_EXPIRED, 0L)
        set(value) = orbBoardPrefs.edit { putLong(ORB_BOARD_EXPIRED, value) }

    var orbBoardAppState: Int
        get() = orbBoardPrefs.getInt(ORB_BOARD_APPLICATION_STATE, 0)
        set(value) = orbBoardPrefs.edit { putInt(ORB_BOARD_APPLICATION_STATE, value) }

    var orbBoardNotificationRequest: Long
        get() = orbBoardPrefs.getLong(ORB_BOARD_NOTIFICAITON_REQUEST, 0L)
        set(value) = orbBoardPrefs.edit { putLong(ORB_BOARD_NOTIFICAITON_REQUEST, value) }


    var orbBoardNotificationState:Int
        get() = orbBoardPrefs.getInt(ORB_BOARD_NOTIFICATION_STATE, 0)
        set(value) = orbBoardPrefs.edit { putInt(ORB_BOARD_NOTIFICATION_STATE, value) }

    companion object {
        private const val ORB_BOARD_NOTIFICATION_STATE = "orbBoardNotificationState"
        private const val ORB_BOARD_SAVED_URL = "orbBoardSavedUrl"
        private const val ORB_BOARD_EXPIRED = "orbBoardExpired"
        private const val ORB_BOARD_APPLICATION_STATE = "orbBoardApplicationState"
        private const val ORB_BOARD_NOTIFICAITON_REQUEST = "orbBoardNotificationRequest"
    }
}