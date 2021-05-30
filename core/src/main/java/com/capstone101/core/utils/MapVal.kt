package com.capstone101.core.utils

import com.capstone101.core.data.db.entities.UserEntity
import com.capstone101.core.data.network.firebase.DangerFire
import com.capstone101.core.data.network.firebase.RelativesFire
import com.capstone101.core.data.network.firebase.UserFire
import com.capstone101.core.domain.model.Danger
import com.capstone101.core.domain.model.Relatives
import com.capstone101.core.domain.model.User

object MapVal {
    @Volatile
    var user: User? = null

    fun userEntToDom(data: UserEntity?): User? {
        if (data == null) return null
        val key = data.key.split(", ")
        return User(
            data.username, data.password, data.email,
            data.address, data.type, key, data.inDanger
        )
    }

    fun userFireToEnt(data: UserFire): UserEntity {
        val key = data.key!!.toList().toString().replace("[", "").replace("]", "")
        return UserEntity(
            data.username!!, data.password!!, data.email!!, data.address,
            data.type ?: 2, key, data.inDanger
        )
    }

    fun userFireToDom(data: UserFire): User =
        User(
            data.username!!, data.password!!, data.email!!, data.address, data.type ?: 2,
            data.key!!, data.inDanger
        )

    fun userDomToFire(data: User): UserFire =
        UserFire(
            data.username, data.password, data.email, data.address, data.type,
            data.key, data.inDanger
        )

    fun userDomToEnt(data: User): UserEntity {
        val key = data.key.toList().toString().replace("[", "").replace("]", "")
        return UserEntity(
            data.username, data.password, data.email, data.address,
            data.type, key, data.inDanger
        )
    }

    fun relativesFireToDom(data: RelativesFire): Relatives =
        Relatives(data.invited ?: listOf(), data.inviting ?: listOf(), data.pure ?: listOf())

    fun dangerDomToFire(data: Danger): DangerFire =
        DangerFire(data.id, data.place, data.record, data.time, data.type)
}