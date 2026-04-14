package com.orbboard.boardoar.nfre.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class OrbBoardDataStore : ViewModel(){
    val orbBoardViList: MutableList<OrbBoardVi> = mutableListOf()
    var orbBoardIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var orbBoardContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var orbBoardView: OrbBoardVi

}