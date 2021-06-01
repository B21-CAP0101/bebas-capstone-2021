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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Repositories(private val db: DBGetData, private val network: NetworkGetData) : IRepositories {
    override fun login(user: User): Flow<Status<User>> =
        object : AccountBoundRes<UserFire, User>() {
            override fun loadDB(): Flow<User> =
                db.getUser()
                    .map {
                        MapVal.userEntToDom(it) ?: User(
                            "", "", "", null,
                            null, null, null, 2, listOf(),
                        )
                    }

            override fun shouldFetch(db: User?): Boolean = true

            override suspend fun netCall(): Flow<NetworkStatus<UserFire>> =
                network.getUser(MapVal.userDomToFire(user))

            override suspend fun saveCallResult(data: UserFire, result: User?) {
                db.insert(MapVal.userFireToEnt(data))
            }
        }.asFlow()

    override fun getUser(): Flow<User?> = db.getUser().map { MapVal.userEntToDom(it) }

    override fun getRelative(callback: (Relatives) -> Unit) {
        network.getRelatives { result ->
            when (result) {
                is NetworkStatus.Success -> callback(MapVal.relativesFireToDom(result.data))
                else -> callback(Relatives(MapVal.user!!.username, listOf(), listOf(), listOf()))
            }
        }
    }

    override fun checkInDanger(callback: (Status<List<User>>) -> Unit) {
        network.checkInDanger { result ->
            when (result) {
                is NetworkStatus.Success -> callback(Status.Success(result.data.map {
                    MapVal.userFireToDom(it)
                }))
                is NetworkStatus.Empty -> callback(Status.Success(listOf<User>()))
                is NetworkStatus.Failed -> callback(Status.Error(null, result.error))
            }
        }
    }

    override suspend fun insertToFs(user: User) = network.insertToFs(MapVal.userDomToFire(user))

    override fun updateUserFS() {
        network.updateUserFS()
    }

    override fun insertDanger(danger: Danger): Boolean =
        network.insertDanger(MapVal.dangerDomToFire(danger))

    override suspend fun updateUser(user: User) = db.update(MapVal.userDomToEnt(user))

    override fun getUserSearch(username: String): Flow<List<User>> =
        network.getUserSearch(username).map { it.map { user -> MapVal.userFireToDom(user) } }

    override fun invitingRelative(relatives: Relatives, target: User, condition: Boolean) =
        network.invitingRelative(
            MapVal.relativesDomToFire(relatives), MapVal.userDomToFire(target), condition
        )

    override fun confirmRelative(relatives: Relatives, target: User, condition: Boolean) =
        network.confirmRelative(
            MapVal.relativesDomToFire(relatives), MapVal.userDomToFire(target), condition
        )

    override fun deleteRelation(relatives: Relatives, target: User) =
        network.deleteRelation(MapVal.relativesDomToFire(relatives), MapVal.userDomToFire(target))

    override fun getLatestDanger(user: User): Flow<Danger> =
        network.getLatestDanger(MapVal.userDomToFire(user)).map { MapVal.dangerFireToDom(it) }

    override fun uploadRecord(filePath: String, fileName: String): Flow<String> =
        network.uploadRecord(filePath, fileName)
}