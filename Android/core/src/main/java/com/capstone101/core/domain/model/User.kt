package com.capstone101.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val password: String,
    val email: String,
    val name: String? = null,
    val address: String? = null,
    val photoURL: String? = null,
    val gender: Boolean? = null,
    val type: Int,
    val key: List<String>,
    var inDanger: Boolean = false
) : Parcelable
