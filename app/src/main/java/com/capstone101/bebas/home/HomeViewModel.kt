package com.capstone101.bebas.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone101.core.domain.usecase.IUseCase

class HomeViewModel(private val useCase: IUseCase) : ViewModel() {
    val getUser = useCase.getUser().asLiveData()
}