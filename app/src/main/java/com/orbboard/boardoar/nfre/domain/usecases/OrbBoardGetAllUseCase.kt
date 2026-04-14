package com.orbboard.boardoar.nfre.domain.usecases

import android.util.Log
import com.orbboard.boardoar.nfre.data.repo.OrbBoardRepository
import com.orbboard.boardoar.nfre.data.utils.OrbBoardPushToken
import com.orbboard.boardoar.nfre.data.utils.OrbBoardSystemService
import com.orbboard.boardoar.nfre.domain.model.OrbBoardEntity
import com.orbboard.boardoar.nfre.domain.model.OrbBoardParam
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication

class OrbBoardGetAllUseCase(
    private val orbBoardRepository: OrbBoardRepository,
    private val orbBoardSystemService: OrbBoardSystemService,
    private val orbBoardPushToken: OrbBoardPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : OrbBoardEntity?{
        val params = OrbBoardParam(
            orbBoardLocale = orbBoardSystemService.orbBoardGetLocale(),
            orbBoardPushToken = orbBoardPushToken.orbBoardGetToken(),
            orbBoardAfId = orbBoardSystemService.orbBoardGetAppsflyerId()
        )
        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Params for request: $params")
        return orbBoardRepository.orbBoardGetClient(params, conversion)
    }



}