package com.capstone101.bebas

import android.app.Application
import com.capstone101.bebas.di.useCaseModule
import com.capstone101.bebas.di.viewModelModule
import com.capstone101.core.di.databaseModule
import com.capstone101.core.di.fireStoreModule
import com.capstone101.core.di.repositoriesModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(
                listOf(
                    databaseModule,
                    fireStoreModule,
                    repositoriesModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}