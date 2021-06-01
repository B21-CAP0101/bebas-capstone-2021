package com.capstone101.bebas.ui.welcome.register

import androidx.lifecycle.*
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.usecase.IUseCase
import com.capstone101.core.utils.Security
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisViewModel(private val useCase: IUseCase) : ViewModel() {
    private val data = MutableLiveData<Boolean?>()
    val condition: LiveData<Boolean?> = data

    fun insertToFs(username: String, dePassword: String, email: String) {
        viewModelScope.launch {
            val password = Security.encrypt(dePassword)
            val key = Security.key.toList()
            val condition: Boolean?
            withContext(Dispatchers.IO) {
                condition = useCase.insertToFs(
                    User(username, password, email, null, null, null, null, 2, key)
                )
            }
            data.value = condition
        }
    }

    fun login(username: String, password: String) =
        useCase.login(User(username, password, "", null, null, null, null, 2, listOf()))
            .asLiveData()
}