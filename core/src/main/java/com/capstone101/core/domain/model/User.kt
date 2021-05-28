package com.capstone101.core.domain.model

data class User(
    val username: String,
    val password: String,
    val email: String,
    val address: String? = null,
    val type: Int,
    val key: List<String>,
    val inDanger: Boolean = false
)
