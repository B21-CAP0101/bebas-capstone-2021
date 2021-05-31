package com.capstone101.core.data

import com.capstone101.core.data.network.NetworkStatus
import kotlinx.coroutines.flow.*

abstract class AccountBoundRes<Req, Res> {
    private val result: Flow<Status<Res>> = flow {
        val db = loadDB().firstOrNull()
        if (shouldFetch(db)) {
            emit(Status.Loading())
            when (val response = netCall().first()) {
                is NetworkStatus.Success -> {
                    saveCallResult(response.data, db)
                    emit(loadDB().map { Status.Success(it) }.first())
                }
                is NetworkStatus.Empty ->
                    emit(loadDB().map { Status.Error(null, "Username atau Password salah") }
                        .first())
                is NetworkStatus.Failed ->
                    emit(loadDB().map { Status.Error(null, response.error) }.first())
            }
        } else emitAll(loadDB().map { Status.Success(it) })
    }

    protected abstract fun loadDB(): Flow<Res>
    protected abstract fun shouldFetch(db: Res?): Boolean
    protected abstract suspend fun netCall(): Flow<NetworkStatus<Req>>
    protected abstract suspend fun saveCallResult(data: Req, result: Res?)
    fun asFlow() = result
}