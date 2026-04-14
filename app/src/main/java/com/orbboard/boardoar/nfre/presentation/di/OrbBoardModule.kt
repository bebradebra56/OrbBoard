package com.orbboard.boardoar.nfre.presentation.di

import com.orbboard.boardoar.nfre.data.repo.OrbBoardRepository
import com.orbboard.boardoar.nfre.data.shar.OrbBoardSharedPreference
import com.orbboard.boardoar.nfre.data.utils.OrbBoardPushToken
import com.orbboard.boardoar.nfre.data.utils.OrbBoardSystemService
import com.orbboard.boardoar.nfre.domain.usecases.OrbBoardGetAllUseCase
import com.orbboard.boardoar.nfre.presentation.pushhandler.OrbBoardPushHandler
import com.orbboard.boardoar.nfre.presentation.ui.load.OrbBoardLoadViewModel
import com.orbboard.boardoar.nfre.presentation.ui.view.OrbBoardViFun
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val orbBoardModule = module {
    factory {
        OrbBoardPushHandler()
    }
    single {
        OrbBoardRepository()
    }
    single {
        OrbBoardSharedPreference(get())
    }
    factory {
        OrbBoardPushToken()
    }
    factory {
        OrbBoardSystemService(get())
    }
    factory {
        OrbBoardGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        OrbBoardViFun(get())
    }
    viewModel {
        OrbBoardLoadViewModel(get(), get(), get())
    }
}