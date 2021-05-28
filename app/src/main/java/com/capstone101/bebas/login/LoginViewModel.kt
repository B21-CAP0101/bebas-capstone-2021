package com.capstone101.bebas.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.usecase.IUseCase

class LoginViewModel(private val useCase: IUseCase) : ViewModel() {
    fun getUser(username: String, password: String) =
        useCase.getUser(User(username, password, "", null, 2, listOf())).asLiveData()
}