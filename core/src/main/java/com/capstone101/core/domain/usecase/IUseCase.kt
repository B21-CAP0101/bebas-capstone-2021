package com.capstone101.core.domain.usecase

import com.capstone101.core.data.Status
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUseCase {
    fun login(user: User): Flow<Status<User>>
    fun getUser(): Flow<User?>
    fun getRelative(): Flow<Relatives>

    suspend fun insertToFs(user: User): Boolean?

    fun updateUserFS()
    fun insertDanger(danger: Danger): Boolean
    suspend fun updateUser(user: User)
}