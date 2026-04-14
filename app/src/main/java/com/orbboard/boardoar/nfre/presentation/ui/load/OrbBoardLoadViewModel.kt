package com.orbboard.boardoar.nfre.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.nfre.data.shar.OrbBoardSharedPreference
import com.orbboard.boardoar.nfre.data.utils.OrbBoardSystemService
import com.orbboard.boardoar.nfre.domain.usecases.OrbBoardGetAllUseCase
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardAppsFlyerState
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrbBoardLoadViewModel(
    private val orbBoardGetAllUseCase: OrbBoardGetAllUseCase,
    private val orbBoardSharedPreference: OrbBoardSharedPreference,
    private val orbBoardSystemService: OrbBoardSystemService
) : ViewModel() {

    private val _orbBoardHomeScreenState: MutableStateFlow<OrbBoardHomeScreenState> =
        MutableStateFlow(OrbBoardHomeScreenState.OrbBoardLoading)
    val orbBoardHomeScreenState = _orbBoardHomeScreenState.asStateFlow()

    private var orbBoardGetApps = false


    init {
        viewModelScope.launch {
            when (orbBoardSharedPreference.orbBoardAppState) {
                0 -> {
                    if (orbBoardSystemService.orbBoardIsOnline()) {
                        OrbBoardApplication.orbBoardConversionFlow.collect {
                            when(it) {
                                OrbBoardAppsFlyerState.OrbBoardDefault -> {}
                                OrbBoardAppsFlyerState.OrbBoardError -> {
                                    orbBoardSharedPreference.orbBoardAppState = 2
                                    _orbBoardHomeScreenState.value =
                                        OrbBoardHomeScreenState.OrbBoardError
                                    orbBoardGetApps = true
                                }
                                is OrbBoardAppsFlyerState.OrbBoardSuccess -> {
                                    if (!orbBoardGetApps) {
                                        orbBoardGetData(it.orbBoardData)
                                        orbBoardGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _orbBoardHomeScreenState.value =
                            OrbBoardHomeScreenState.OrbBoardNotInternet
                    }
                }
                1 -> {
                    if (orbBoardSystemService.orbBoardIsOnline()) {
                        if (OrbBoardApplication.ORB_BOARD_FB_LI != null) {
                            _orbBoardHomeScreenState.value =
                                OrbBoardHomeScreenState.OrbBoardSuccess(
                                    OrbBoardApplication.ORB_BOARD_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > orbBoardSharedPreference.orbBoardExpired) {
                            Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Current time more then expired, repeat request")
                            OrbBoardApplication.orbBoardConversionFlow.collect {
                                when(it) {
                                    OrbBoardAppsFlyerState.OrbBoardDefault -> {}
                                    OrbBoardAppsFlyerState.OrbBoardError -> {
                                        _orbBoardHomeScreenState.value =
                                            OrbBoardHomeScreenState.OrbBoardSuccess(
                                                orbBoardSharedPreference.orbBoardSavedUrl
                                            )
                                        orbBoardGetApps = true
                                    }
                                    is OrbBoardAppsFlyerState.OrbBoardSuccess -> {
                                        if (!orbBoardGetApps) {
                                            orbBoardGetData(it.orbBoardData)
                                            orbBoardGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Current time less then expired, use saved url")
                            _orbBoardHomeScreenState.value =
                                OrbBoardHomeScreenState.OrbBoardSuccess(
                                    orbBoardSharedPreference.orbBoardSavedUrl
                                )
                        }
                    } else {
                        _orbBoardHomeScreenState.value =
                            OrbBoardHomeScreenState.OrbBoardNotInternet
                    }
                }
                2 -> {
                    _orbBoardHomeScreenState.value =
                        OrbBoardHomeScreenState.OrbBoardError
                }
            }
        }
    }


    private suspend fun orbBoardGetData(conversation: MutableMap<String, Any>?) {
        val orbBoardData = orbBoardGetAllUseCase.invoke(conversation)
        if (orbBoardSharedPreference.orbBoardAppState == 0) {
            if (orbBoardData == null) {
                orbBoardSharedPreference.orbBoardAppState = 2
                _orbBoardHomeScreenState.value =
                    OrbBoardHomeScreenState.OrbBoardError
            } else {
                orbBoardSharedPreference.orbBoardAppState = 1
                orbBoardSharedPreference.apply {
                    orbBoardExpired = orbBoardData.orbBoardExpires
                    orbBoardSavedUrl = orbBoardData.orbBoardUrl
                }
                _orbBoardHomeScreenState.value =
                    OrbBoardHomeScreenState.OrbBoardSuccess(orbBoardData.orbBoardUrl)
            }
        } else  {
            if (orbBoardData == null) {
                _orbBoardHomeScreenState.value =
                    OrbBoardHomeScreenState.OrbBoardSuccess(
                        orbBoardSharedPreference.orbBoardSavedUrl
                    )
            } else {
                orbBoardSharedPreference.apply {
                    orbBoardExpired = orbBoardData.orbBoardExpires
                    orbBoardSavedUrl = orbBoardData.orbBoardUrl
                }
                _orbBoardHomeScreenState.value =
                    OrbBoardHomeScreenState.OrbBoardSuccess(orbBoardData.orbBoardUrl)
            }
        }
    }


    sealed class OrbBoardHomeScreenState {
        data object OrbBoardLoading : OrbBoardHomeScreenState()
        data object OrbBoardError : OrbBoardHomeScreenState()
        data class OrbBoardSuccess(val data: String) : OrbBoardHomeScreenState()
        data object OrbBoardNotInternet: OrbBoardHomeScreenState()
    }
}