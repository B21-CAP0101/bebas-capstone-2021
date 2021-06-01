package com.capstone101.core.data.network

import android.net.Uri
import com.capstone101.core.data.network.firebase.DangerFire
import com.capstone101.core.data.network.firebase.RelativesFire
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.utils.MapVal
import com.capstone101.core.utils.Security
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.io.File

class NetworkGetData(private val fs: FirebaseFirestore, private val storage: FirebaseStorage) {
    suspend fun getUser(check: UserFire): Flow<NetworkStatus<UserFire>> = flow {
        if (check(check.username!!)) {
            val user = fs.collection(UserFire.COLLECTION).document(check.username).get().await()
                .toObject(UserFire::class.java)
            if (Security.decrypt(user?.password!!, user.key!!.toTypedArray()) == check.password)
                emit(NetworkStatus.Success(user))
            else emit(NetworkStatus.Empty)
        } else emit(NetworkStatus.Empty)
    }.flowOn(Dispatchers.IO)

    fun getRelatives(networkStatus: (NetworkStatus<RelativesFire>) -> Unit) {
        val user = MapVal.user!!
        fs.collection(RelativesFire.COLLECTION).document(user.username)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    networkStatus(NetworkStatus.Failed("Error occurred\n${e.code}"))
                    return@addSnapshotListener
                }
                if (value == null) networkStatus(NetworkStatus.Empty)
                else {
                    val relatives = value.toObject(RelativesFire::class.java) ?: RelativesFire()
                    networkStatus(NetworkStatus.Success(relatives))
                }
            }
    }

    fun invitingRelative(relatives: RelativesFire, target: UserFire, condition: Boolean) {
        fs.collection(RelativesFire.COLLECTION).document(target.username!!).get()
            .addOnSuccessListener {
                val relativesTarget = it.toObject(RelativesFire::class.java)
                    ?.apply { this.username = target.username } ?: RelativesFire(target.username)
                val userInviting = relatives.inviting!!.toMutableList()
                val targetInvited = relativesTarget.invited?.toMutableList() ?: mutableListOf()
                if (condition) {
                    userInviting.add(target.username)
                    targetInvited.add(relatives.username!!)
                    relatives.inviting = userInviting.toList()
                    relativesTarget.invited = targetInvited.toList()
                } else {
                    userInviting.remove(target.username)
                    targetInvited.remove(relatives.username!!)
                    relatives.inviting = userInviting.toList()
                    relativesTarget.invited = targetInvited.toList()
                }
                fs.collection(RelativesFire.COLLECTION).document(target.username)
                    .set(relativesTarget)
                fs.collection(RelativesFire.COLLECTION).document(relatives.username!!)
                    .set(relatives)
            }
    }

    fun confirmRelative(relatives: RelativesFire, target: UserFire, condition: Boolean) {
        fs.collection(RelativesFire.COLLECTION).document(target.username!!).get()
            .addOnSuccessListener {
                val relativesTarget = it.toObject(RelativesFire::class.java)
                    ?.apply { this.username = target.username } ?: RelativesFire(target.username)
                val targetInviting = relativesTarget.inviting?.toMutableList() ?: mutableListOf()
                val userInvited = relatives.invited!!.toMutableList()
                if (condition) {
                    val targetPure = relativesTarget.pure?.toMutableList() ?: mutableListOf()
                    val userPure = relatives.pure!!.toMutableList()
                    targetPure.add(relatives.username!!)
                    userPure.add(target.username)
                    relatives.pure = userPure.toList()
                    relativesTarget.pure = targetPure.toList()
                }
                targetInviting.remove(relatives.username!!)
                userInvited.remove(target.username)
                relatives.invited = userInvited.toList()
                relativesTarget.inviting = targetInviting.toList()
                fs.collection(RelativesFire.COLLECTION).document(target.username)
                    .set(relativesTarget)
                fs.collection(RelativesFire.COLLECTION).document(relatives.username!!)
                    .set(relatives)
            }
    }

    fun deleteRelation(relatives: RelativesFire, target: UserFire) {
        fs.collection(RelativesFire.COLLECTION).document(target.username!!).get()
            .addOnSuccessListener {
                val relativesTarget = it.toObject(RelativesFire::class.java)
                    ?.apply { this.username = target.username } ?: RelativesFire(target.username)
                val targetPure = relativesTarget.pure?.toMutableList() ?: mutableListOf()
                val userPure = relatives.pure!!.toMutableList()
                targetPure.remove(relatives.username!!)
                userPure.remove(target.username)
                relativesTarget.pure = targetPure
                relatives.pure = userPure
                fs.collection(RelativesFire.COLLECTION).document(target.username)
                    .set(relativesTarget)
                fs.collection(RelativesFire.COLLECTION).document(relatives.username!!)
                    .set(relatives)
            }
    }

    fun checkInDanger(networkStatus: (NetworkStatus<List<UserFire>>) -> Unit) {
        fs.collection(UserFire.COLLECTION).whereEqualTo(UserFire.DANGER, true)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    networkStatus(NetworkStatus.Failed("Error occurred\n${e.code}"))
                    return@addSnapshotListener
                }
                if (value == null || value.isEmpty) networkStatus(NetworkStatus.Empty)
                else {
                    val users = value.map { it.toObject(UserFire::class.java) }
                    networkStatus(NetworkStatus.Success(users))
                }
            }
    }

    fun getLatestDanger(user: UserFire) = flow {
        val result = fs.collection(DangerFire.COLLECTION)
            .document(user.username!!)
            .collection(DangerFire.SUB_COLLECTION)
            .orderBy(DangerFire.TIME, Query.Direction.DESCENDING)
            .limit(1).get().await()
        emit(result.documents[0].toObject(DangerFire::class.java)!!)
    }

    suspend fun insertToFs(user: UserFire): Boolean? = try {
        if (check(user.username!!)) null
        else {
            val input =
                UserFire(
                    user.username, user.password, user.email, user.name,
                    user.address, user.type, user.key
                )
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
        val userID = hashMapOf("user_id" to user.username)
        fs.collection(DangerFire.COLLECTION).document(user.username!!).set(userID)
        fs.collection(DangerFire.COLLECTION).document(user.username)
            .collection(DangerFire.SUB_COLLECTION).document(danger.id!!).set(danger)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun uploadRecord(filePath: String, fileName: String): Flow<String> = flow {
        if (MapVal.user == null) emit("")
        val file = Uri.fromFile(File(filePath))
        val storageRef = storage.reference.child("${MapVal.user!!.username}/record/$fileName")
        val downloadURL = storageRef.putFile(file).await().storage.downloadUrl.await().toString()
        emit(downloadURL)
    }

    private suspend fun check(username: String): Boolean =
        fs.collection(UserFire.COLLECTION).get().await().documents.any { it.id == username }
}