package com.orbboard.boardoar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.orbboard.boardoar.nfre.OrbBoardGlobalLayoutUtil
import com.orbboard.boardoar.nfre.orbBoardSetupSystemBars
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication
import com.orbboard.boardoar.nfre.presentation.pushhandler.OrbBoardPushHandler
import org.koin.android.ext.android.inject

class OrbBoardActivity : AppCompatActivity() {

    private val orbBoardPushHandler by inject<OrbBoardPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orbBoardSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_orb_board)

        val orbBoardRootView = findViewById<View>(android.R.id.content)
        OrbBoardGlobalLayoutUtil().orbBoardAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(orbBoardRootView) { orbBoardView, orbBoardInsets ->
            val orbBoardSystemBars = orbBoardInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val orbBoardDisplayCutout = orbBoardInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val orbBoardIme = orbBoardInsets.getInsets(WindowInsetsCompat.Type.ime())


            val orbBoardTopPadding = maxOf(orbBoardSystemBars.top, orbBoardDisplayCutout.top)
            val orbBoardLeftPadding = maxOf(orbBoardSystemBars.left, orbBoardDisplayCutout.left)
            val orbBoardRightPadding = maxOf(orbBoardSystemBars.right, orbBoardDisplayCutout.right)
            window.setSoftInputMode(OrbBoardApplication.orbBoardInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "ADJUST PUN")
                val orbBoardBottomInset = maxOf(orbBoardSystemBars.bottom, orbBoardDisplayCutout.bottom)

                orbBoardView.setPadding(orbBoardLeftPadding, orbBoardTopPadding, orbBoardRightPadding, 0)

                orbBoardView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = orbBoardBottomInset
                }
            } else {
                Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "ADJUST RESIZE")

                val orbBoardBottomInset = maxOf(orbBoardSystemBars.bottom, orbBoardDisplayCutout.bottom, orbBoardIme.bottom)

                orbBoardView.setPadding(orbBoardLeftPadding, orbBoardTopPadding, orbBoardRightPadding, 0)

                orbBoardView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = orbBoardBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Activity onCreate()")
        orbBoardPushHandler.orbBoardHandlePush(intent.extras)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            orbBoardSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        orbBoardSetupSystemBars()
    }
}