package com.capstone101.bebas

import android.app.Application
import android.content.Intent
import android.os.Build
import com.capstone101.bebas.background.service.AutoService
import com.capstone101.bebas.di.useCaseModule
import com.capstone101.bebas.di.viewModelModule
import com.capstone101.core.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, AutoService::class.java))
        else startService(Intent(this, AutoService::class.java))

        startKoin {
            androidContext(this@MyApp)
            modules(
                listOf(
                    databaseModule,
                    firebaseModule,
                    repositoriesModule,
                    sessionModule,
                    useCaseModule,
                    viewModelModule,
                    fusedLocationProviderClient
                )
            )
        }
    }
}