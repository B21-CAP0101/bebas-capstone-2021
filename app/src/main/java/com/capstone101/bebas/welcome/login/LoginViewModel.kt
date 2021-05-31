package com.capstone101.bebas.welcome.login

import androidx.lifecycle.ViewModel
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.usecase.IUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class LoginViewModel(private val useCase: IUseCase) : ViewModel() {
    fun login(username: String, password: String) =
        useCase.login(User(username, password, "", null, null, 2, listOf())).flowOn(Dispatchers.IO)
}