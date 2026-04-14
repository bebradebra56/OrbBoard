package com.orbboard.boardoar.nfre

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication

class OrbBoardGlobalLayoutUtil {

    private var orbBoardMChildOfContent: View? = null
    private var orbBoardUsableHeightPrevious = 0

    fun orbBoardAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        orbBoardMChildOfContent = content.getChildAt(0)

        orbBoardMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val orbBoardUsableHeightNow = orbBoardComputeUsableHeight()
        if (orbBoardUsableHeightNow != orbBoardUsableHeightPrevious) {
            val orbBoardUsableHeightSansKeyboard = orbBoardMChildOfContent?.rootView?.height ?: 0
            val orbBoardHeightDifference = orbBoardUsableHeightSansKeyboard - orbBoardUsableHeightNow

            if (orbBoardHeightDifference > (orbBoardUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(OrbBoardApplication.orbBoardInputMode)
            } else {
                activity.window.setSoftInputMode(OrbBoardApplication.orbBoardInputMode)
            }
//            mChildOfContent?.requestLayout()
            orbBoardUsableHeightPrevious = orbBoardUsableHeightNow
        }
    }

    private fun orbBoardComputeUsableHeight(): Int {
        val r = Rect()
        orbBoardMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}