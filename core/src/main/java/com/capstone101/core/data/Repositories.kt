package com.capstone101.core.data

import com.capstone101.core.data.db.DBGetData
import com.capstone101.core.data.network.NetworkGetData
import com.capstone101.core.data.network.NetworkStatus
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.domain.model.User
import com.capstone101.core.domain.repositories.IRepositories
import com.capstone101.core.utils.MapVal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Repositories(private val db: DBGetData, private val network: NetworkGetData) : IRepositories {
    override fun getUser(user: User): Flow<Status<User>> =
        object : NetworkBoundRes<UserFire, User>() {
            override fun loadDB(): Flow<User> =
                db.getUser().map { MapVal.userEntToDom(it) ?: User("", "", "", null, 2, listOf()) }

            override fun shouldFetch(db: User?): Boolean = true

            override suspend fun netCall(): Flow<NetworkStatus<UserFire>> =
                network.getUser(MapVal.userDomToFire(user))

            override suspend fun saveCallResult(data: UserFire, result: User?) {
                db.insert(MapVal.userFireToEnt(data))
            }
        }.asFlow()

    override suspend fun insertToFs(user: User) = network.insertToFs(MapVal.userDomToFire(user))
}