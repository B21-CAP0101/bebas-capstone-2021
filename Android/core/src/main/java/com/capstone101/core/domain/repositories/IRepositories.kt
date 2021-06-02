package com.capstone101.core.domain.repositories

import com.capstone101.core.data.Status
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IRepositories {
    fun login(user: User): Flow<Status<User>>
    fun getUser(): Flow<User?>
    fun getRelative(callback: (Relatives) -> Unit)
    fun checkInDanger(callback: (Status<List<User>>) -> Unit)

    suspend fun insertToFs(user: User): Boolean?
    fun insertDanger(danger: Danger): Boolean

    fun updateUserFS()
    suspend fun updateUser(user: User)

    fun getUserInfoForRelatives(relatives: Relatives, type: String): Flow<List<User>>
    fun getUserSearch(username: String): Flow<List<User>>
    fun invitingRelative(relatives: Relatives, target: User, condition: Boolean)
    fun confirmRelative(relatives: Relatives, target: User, condition: Boolean)
    fun deleteRelation(relatives: Relatives, target: User)

    fun getLatestDanger(user: User): Flow<Danger>

    fun uploadRecord(filePath: String, fileName: String): Flow<String>
}