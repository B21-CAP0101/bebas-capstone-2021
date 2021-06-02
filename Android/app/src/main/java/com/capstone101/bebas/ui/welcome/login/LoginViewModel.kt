package com.capstone101.bebas.ui.welcome.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.usecase.IUseCase

class LoginViewModel(private val useCase: IUseCase) : ViewModel() {
    fun login(username: String, password: String) =
        useCase.login(User(username, password, "", "", null, null, false, 2, listOf()))
            .asLiveData()
}