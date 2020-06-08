package com.gmail.kenzhang0

import android.app.Application
import com.gmail.kenzhang0.di.Injector
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin

class MyApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(Injector.module())
            androidLogger()
        }
    }

}