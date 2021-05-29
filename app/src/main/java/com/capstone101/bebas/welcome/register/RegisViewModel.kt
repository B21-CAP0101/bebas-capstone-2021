package com.capstone101.bebas.welcome.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    User(username, password, email, null, 2, key)
                )
            }
            data.value = condition
        }
    }
}