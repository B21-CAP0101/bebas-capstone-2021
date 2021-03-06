package com.capstone101.bebas.ui.main.relative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.usecase.IUseCase

class RelativeViewModel(private val useCase: IUseCase) : ViewModel() {
    fun getRelative(callback: (Relatives) -> Unit) = useCase.getRelative { callback(it) }

    fun getUserInfoByRelative(relatives: Relatives, type: String) =
        useCase.getUserInfoForRelatives(relatives, type).asLiveData()

    fun searchUser(username: String) = useCase.getUserSearch(username).asLiveData()

    fun invite(relatives: Relatives, target: User, condition: Boolean) =
        useCase.invitingRelative(relatives, target, condition)

    fun confirm(relatives: Relatives, target: User, condition: Boolean) =
        useCase.confirmRelative(relatives, target, condition)

    fun delete(relatives: Relatives, target: User) = useCase.deleteRelation(relatives, target)
}