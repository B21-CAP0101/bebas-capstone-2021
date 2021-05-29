package com.capstone101.core.data.network

import com.capstone101.core.data.network.firebase.RelativesFire
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.utils.Security
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class NetworkGetData(private val fs: FirebaseFirestore) {
    suspend fun getUser(check: UserFire): Flow<NetworkStatus<UserFire>> = flow {
        if (check(check.username!!)) {
            val user = fs.collection(UserFire.COLLECTION).document(check.username).get().await()
                .toObject(UserFire::class.java)
            if (Security.decrypt(user?.password!!, user.key!!.toTypedArray()) == check.password)
                emit(NetworkStatus.Success(user))
            else emit(NetworkStatus.Empty)
        } else emit(NetworkStatus.Empty)
    }.flowOn(Dispatchers.IO)

    suspend fun getRelatives(): Flow<NetworkStatus<RelativesFire>> = flow {

    }.flowOn(Dispatchers.IO)

    suspend fun insertToFs(user: UserFire): Boolean? {
        return try {
            if (check(user.username!!)) null
            else {
                val password = Security.encrypt(user.password!!)
                val input =
                    UserFire(user.username, password, user.email, user.address, user.type, user.key)
                fs.collection(UserFire.COLLECTION).document(input.username!!).set(user)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun check(username: String): Boolean =
        fs.collection(UserFire.COLLECTION).get().await().documents.any { it.id == username }
}