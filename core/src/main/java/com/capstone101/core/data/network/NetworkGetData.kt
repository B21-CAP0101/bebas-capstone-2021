package com.capstone101.core.data.network

import com.capstone101.core.data.network.firebase.DangerFire
import com.capstone101.core.data.network.firebase.RelativesFire
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.utils.MapVal
import com.capstone101.core.utils.Security
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
        val user = MapVal.user!!
        val relatives =
            fs.collection(RelativesFire.COLLECTION).document(user.username).get().await()
                .toObject(RelativesFire::class.java)
        if (relatives == null) emit(NetworkStatus.Empty)
        else emit(NetworkStatus.Success(relatives))
    }.flowOn(Dispatchers.IO)

    @ExperimentalCoroutinesApi
    fun checkInDanger(): Flow<NetworkStatus<List<UserFire>>> = callbackFlow {
        val check = fs.collection(UserFire.COLLECTION).whereEqualTo(UserFire.DANGER, true)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    trySend(NetworkStatus.Failed("Error occurred"))
                    return@addSnapshotListener
                }
                if (value == null || value.isEmpty) trySend(NetworkStatus.Empty)
                else {
                    val users = value.map { it.toObject(UserFire::class.java) }
                    trySend(NetworkStatus.Success(users))
                }
            }
        awaitClose { check.remove() }
    }.flowOn(Dispatchers.IO)

    suspend fun insertToFs(user: UserFire): Boolean? = try {
        if (check(user.username!!)) null
        else {
            val password = Security.encrypt(user.password!!)
            val input =
                UserFire(user.username, password, user.email, user.address, user.type, user.key)
            fs.collection(UserFire.COLLECTION).document(input.username!!).set(input)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun updateUserFS(): UserFire {
        val user = MapVal.userDomToFire(MapVal.user!!)
        fs.collection(UserFire.COLLECTION).document(user.username!!).set(user)
        return user
    }

    fun insertDanger(danger: DangerFire): Boolean = try {
        val user = updateUserFS()
        println(user)
        val userID = hashMapOf("user_id" to user.username)
        fs.collection(DangerFire.COLLECTION).document(user.username!!).set(userID)
        fs.collection(DangerFire.COLLECTION).document(user.username)
            .collection(DangerFire.SUB_COLLECTION).document(danger.id!!).set(danger)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    private suspend fun check(username: String): Boolean =
        fs.collection(UserFire.COLLECTION).get().await().documents.any { it.id == username }
}