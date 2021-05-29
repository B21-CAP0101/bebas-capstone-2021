package com.capstone101.bebas.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone101.core.domain.usecase.IUseCase

class MainViewModel(useCase: IUseCase) : ViewModel() {
    val getUser = useCase.getUser().asLiveData()
}