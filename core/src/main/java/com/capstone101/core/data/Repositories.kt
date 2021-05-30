package com.capstone101.core.data

import com.capstone101.core.data.db.DBGetData
import com.capstone101.core.data.network.NetworkGetData
import com.capstone101.core.data.network.NetworkStatus
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.repositories.IRepositories
import com.capstone101.core.utils.MapVal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class Repositories(private val db: DBGetData, private val network: NetworkGetData) : IRepositories {
    override fun login(user: User): Flow<Status<User>> =
        object : AccountBoundRes<UserFire, User>() {
            override fun loadDB(): Flow<User> =
                db.getUser().map { MapVal.userEntToDom(it) ?: User("", "", "", null, 2, listOf()) }

            override fun shouldFetch(db: User?): Boolean = true

            override suspend fun netCall(): Flow<NetworkStatus<UserFire>> =
                network.getUser(MapVal.userDomToFire(user))

            override suspend fun saveCallResult(data: UserFire, result: User?) {
                db.insert(MapVal.userFireToEnt(data))
            }
        }.asFlow()

    override fun getUser(): Flow<User?> = db.getUser().map { MapVal.userEntToDom(it) }

    override fun getRelative(): Flow<Relatives> = flow {
        when (val result = network.getRelatives().first()) {
            is NetworkStatus.Success -> emit(MapVal.relativesFireToDom(result.data))
            else -> emit(Relatives(listOf(), listOf(), listOf()))
        }
    }

    @ExperimentalCoroutinesApi
    override fun checkInDanger(): Flow<Status<List<User>>> = flow {
        when (val result = network.checkInDanger().first()) {
            is NetworkStatus.Success -> emit(Status.Success(result.data.map {
                MapVal.userFireToDom(
                    it
                )
            }))
            is NetworkStatus.Empty -> emit(Status.Success(listOf<User>()))
            is NetworkStatus.Failed -> emit(Status.Error(null, result.error))
        }
    }

    override suspend fun insertToFs(user: User) = network.insertToFs(MapVal.userDomToFire(user))

    override fun updateUserFS() {
        network.updateUserFS()
    }

    override fun insertDanger(danger: Danger): Boolean =
        network.insertDanger(MapVal.dangerDomToFire(danger))

    override suspend fun updateUser(user: User) = db.update(MapVal.userDomToEnt(user))
}