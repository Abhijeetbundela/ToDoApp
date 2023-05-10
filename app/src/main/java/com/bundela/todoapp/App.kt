package com.bundela.todoapp

import android.app.Application
import android.util.Log
import com.bundela.todoapp.di.module.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ksp.generated.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
//            androidLogger()
            androidContext(this@App)
            modules(AppModule().module)
        }
    }

}