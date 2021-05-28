package com.capstone101.core.domain.repositories

import com.capstone101.core.data.Status
import com.capstone101.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IRepositories {
    fun getUser(user: User): Flow<Status<User>>

    suspend fun insertToFs(user: User): Boolean?
}