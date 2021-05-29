package com.capstone101.core.domain.repositories

import com.capstone101.core.data.Status
import com.capstone101.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IRepositories {
    fun login(user: User): Flow<Status<User>>
    fun getUser(): Flow<User?>

    suspend fun insertToFs(user: User): Boolean?
}