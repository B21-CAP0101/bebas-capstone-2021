package com.capstone101.core.data.db

import com.capstone101.core.data.db.entities.UserEntity
import com.capstone101.core.data.db.room.UserDao
import kotlinx.coroutines.flow.Flow

class DBGetData(private val dao: UserDao) {
    fun getUser(): Flow<UserEntity?> = dao.getUser()

    suspend fun insert(user: UserEntity) = dao.insert(user)

    suspend fun update(user: UserEntity) = dao.update(user)
}