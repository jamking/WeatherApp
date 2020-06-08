package com.gmail.kenzhang0.di

import org.koin.core.module.Module


object Injector {
    fun module(): List<Module> {
        return listOf(
            ApiModule,
            viewModelModule
        )
    }
}