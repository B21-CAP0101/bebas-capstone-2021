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
    override fun getRelative(): Flow<Relatives> = repositories.getRelative()

    override suspend fun insertToFs(user: User): Boolean? = repositories.insertToFs(user)

    override fun updateUserFS() = repositories.updateUserFS()

    override fun insertDanger(danger: Danger): Boolean =
        repositories.insertDanger(danger)

    override suspend fun updateUser(user: User) = repositories.updateUser(user)
}