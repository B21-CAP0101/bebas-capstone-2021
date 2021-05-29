package com.capstone101.core.data

import com.capstone101.core.data.network.NetworkStatus
import kotlinx.coroutines.flow.*

abstract class NetworkBoundRes<Req, Res> {
    private val result: Flow<Status<Res>> = flow {
        val db = loadDB().firstOrNull()
        if (shouldFetch(db)) {
            emit(Status.Loading())
            when (val response = netCall().first()) {
                is NetworkStatus.Success -> {
                    saveCallResult(response.data, db)
                    emitAll(loadDB().map { Status.Success(it) })
                }
                is NetworkStatus.Empty ->
                    emitAll(loadDB().map { Status.Error(it, "username or password wrong") })
                is NetworkStatus.Failed ->
                    emitAll(loadDB().map { Status.Error(it, response.error) })
            }
        } else emitAll(loadDB().map { Status.Success(it) })
    }

    protected abstract fun loadDB(): Flow<Res>
    protected abstract fun shouldFetch(db: Res?): Boolean
    protected abstract suspend fun netCall(): Flow<NetworkStatus<Req>>
    protected abstract suspend fun saveCallResult(data: Req, result: Res?)
    fun asFlow() = result
}