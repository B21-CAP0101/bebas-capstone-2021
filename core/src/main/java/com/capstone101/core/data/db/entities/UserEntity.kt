package com.capstone101.core.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val email: String,
    val address: String? = null,
    val type: Int,
    val key: String,
    val inDanger: Boolean = false
)