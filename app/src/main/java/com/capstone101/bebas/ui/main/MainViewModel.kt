package com.capstone101.bebas.ui.main

import androidx.lifecycle.*
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.domain.usecase.IUseCase
import com.capstone101.core.utils.MapVal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val useCase: IUseCase) : ViewModel() {
    val getUser = useCase.getUser().asLiveData()

    val getRelative = useCase.getRelative().asLiveData()

    val setCondition =
        MutableLiveData<MutableList<Boolean>>().apply { this.value = mutableListOf(false, false) }
    val condition: LiveData<MutableList<Boolean>> = setCondition

    fun insertDanger(danger: Danger) {
        val user = MapVal.user!!.apply { inDanger = true }
        viewModelScope.launch(Dispatchers.IO) { useCase.updateUser(user) }
        useCase.insertDanger(danger)
    }

    fun updateUserStatus() {
        viewModelScope.launch(Dispatchers.IO) { useCase.updateUser(MapVal.user!!) }
        useCase.updateUserFS()
    }
}