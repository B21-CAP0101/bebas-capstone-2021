package com.capstone101.bebas.ui.main.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.usecase.IUseCase

class DetailDangerViewModel(private val useCase: IUseCase) : ViewModel() {
    fun getLatestDanger(user: User) = useCase.getLatestDanger(user).asLiveData()
}