package com.orbboard.boardoar.nfre.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication

class OrbBoardPushHandler {
    fun orbBoardHandlePush(extras: Bundle?) {
        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = orbBoardBundleToMap(extras)
            Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    OrbBoardApplication.ORB_BOARD_FB_LI = map["url"]
                    Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Push data no!")
        }
    }

    private fun orbBoardBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}