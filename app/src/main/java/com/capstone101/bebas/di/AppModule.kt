package com.capstone101.bebas.di

import com.capstone101.bebas.main.MainViewModel
import com.capstone101.bebas.relative.RelativeViewModel
import com.capstone101.bebas.welcome.login.LoginViewModel
import com.capstone101.bebas.welcome.register.RegisViewModel
import com.capstone101.core.domain.usecase.IUseCase
import com.capstone101.core.domain.usecase.UseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    single<IUseCase> { UseCase(get()) }
}

val viewModelModule = module {
    viewModel { RegisViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { RelativeViewModel(get()) }
}