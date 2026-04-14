package com.orbboard.boardoar.nfre.data.utils

import android.util.Log
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class OrbBoardPushToken {

    suspend fun orbBoardGetToken(
        orbBoardMaxAttempts: Int = 3,
        orbBoardDelayMs: Long = 1500
    ): String {

        repeat(orbBoardMaxAttempts - 1) {
            try {
                val orbBoardToken = FirebaseMessaging.getInstance().token.await()
                return orbBoardToken
            } catch (e: Exception) {
                Log.e(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(orbBoardDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}