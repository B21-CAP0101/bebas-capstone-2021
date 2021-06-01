package com.capstone101.core.domain.usecase

import com.capstone101.core.data.Status
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.repositories.IRepositories
import kotlinx.coroutines.flow.Flow

class UseCase(private val repositories: IRepositories) : IUseCase {
    override fun login(user: User): Flow<Status<User>> = repositories.login(user)
    override fun getUser(): Flow<User?> = repositories.getUser()
    override fun getRelative(callback: (Relatives) -> Unit) =
        repositories.getRelative { callback(it) }

    override fun checkInDanger(callback: (Status<List<User>>) -> Unit) =
        repositories.checkInDanger { callback(it) }

    override suspend fun insertToFs(user: User): Boolean? = repositories.insertToFs(user)

    override fun updateUserFS() = repositories.updateUserFS()

    override fun insertDanger(danger: Danger): Boolean =
        repositories.insertDanger(danger)

    override suspend fun updateUser(user: User) = repositories.updateUser(user)

    override fun getUserSearch(username: String): Flow<List<User>> =
        repositories.getUserSearch(username)

    override fun invitingRelative(relatives: Relatives, target: User, condition: Boolean) =
        repositories.invitingRelative(relatives, target, condition)

    override fun confirmRelative(relatives: Relatives, target: User, condition: Boolean) =
        repositories.confirmRelative(relatives, target, condition)

    override fun deleteRelation(relatives: Relatives, target: User) =
        repositories.deleteRelation(relatives, target)

    override fun getLatestDanger(user: User): Flow<Danger> = repositories.getLatestDanger(user)

    override fun uploadRecord(filePath: String, fileName: String): Flow<String> =
        repositories.uploadRecord(filePath, fileName)
}