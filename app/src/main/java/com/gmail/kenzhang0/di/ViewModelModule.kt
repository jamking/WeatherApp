package com.gmail.kenzhang0.di


import com.gmail.kenzhang0.common.AndroidSchedulerProvider
import com.gmail.kenzhang0.common.SchedulerProvider
import com.gmail.kenzhang0.ui.RecentSearchViewModel
import com.gmail.kenzhang0.ui.WeatherSearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


val viewModelModule: Module = module {
    single { provideSchedulerProvider() }
    viewModel { WeatherSearchViewModel() }
    viewModel { RecentSearchViewModel(get()) }
}

fun provideSchedulerProvider(): SchedulerProvider {
    return AndroidSchedulerProvider()
}