package com.orbboard.boardoar.di

import androidx.room.Room
import com.orbboard.boardoar.data.local.database.OrbDatabase
import com.orbboard.boardoar.data.preferences.AppPreferences
import com.orbboard.boardoar.data.repository.CategoryRepositoryImpl
import com.orbboard.boardoar.data.repository.FocusRepositoryImpl
import com.orbboard.boardoar.data.repository.OrbRepositoryImpl
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.FocusRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import com.orbboard.boardoar.presentation.activity.ActivityViewModel
import com.orbboard.boardoar.presentation.board.BoardViewModel
import com.orbboard.boardoar.presentation.categories.CategoriesViewModel
import com.orbboard.boardoar.presentation.create.CreateOrbViewModel
import com.orbboard.boardoar.presentation.detail.OrbDetailViewModel
import com.orbboard.boardoar.presentation.focus.FocusViewModel
import com.orbboard.boardoar.presentation.search.SearchViewModel
import com.orbboard.boardoar.presentation.settings.SettingsViewModel
import com.orbboard.boardoar.presentation.stats.StatsViewModel
import com.orbboard.boardoar.util.BackupManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            OrbDatabase::class.java,
            OrbDatabase.DATABASE_NAME
        ).build()
    }

    single { get<OrbDatabase>().orbDao() }
    single { get<OrbDatabase>().categoryDao() }
    single { get<OrbDatabase>().subTaskDao() }
    single { get<OrbDatabase>().focusSessionDao() }

    single { AppPreferences(androidContext()) }
    single { BackupManager(androidContext()) }

    single<OrbRepository> { OrbRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<FocusRepository> { FocusRepositoryImpl(get(), get()) }

    viewModel { BoardViewModel(get(), get()) }
    viewModel { (orbId: Long) -> CreateOrbViewModel(get(), get(), orbId) }
    viewModel { CategoriesViewModel(get(), get()) }
    viewModel { (orbId: Long) -> OrbDetailViewModel(get(), get(), orbId) }
    viewModel { StatsViewModel(get(), get()) }
    viewModel { (orbId: Long) -> FocusViewModel(get(), get(), orbId) }
    viewModel { ActivityViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
