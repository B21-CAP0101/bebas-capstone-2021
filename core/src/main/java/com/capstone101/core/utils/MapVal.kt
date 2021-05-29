package com.capstone101.core.utils

import com.capstone101.core.data.db.entities.UserEntity
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.domain.model.User

object MapVal {
    @Volatile
    var user: User? = null

    fun userEntToDom(data: UserEntity?): User? {
        if (data == null) return null
        val key = data.key.split(", ")
        return User(data.username, data.password, data.email, data.address, data.type, key)
    }

//    fun userDomToEnt(data: User): UserEntity =
//        UserEntity(data.username, data.password, data.email, data.address, data.type)

    fun userFireToEnt(data: UserFire): UserEntity {
        val key = data.key!!.toList().toString().replace("[", "").replace("]", "")
        return UserEntity(
            data.username!!, data.password!!, data.email!!, data.address,
            data.type ?: 2, key
        )
    }

    fun userDomToFire(data: User): UserFire =
        UserFire(data.username, data.password, data.email, data.address, data.type, data.key)
}