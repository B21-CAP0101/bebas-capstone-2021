package com.capstone101.core.data.network.firebase

data class UserFire(
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val address: String? = null,
    val type: Int? = null,
    val key: List<String>? = null,
    var inDanger: Boolean = false
) {
    companion object {
        const val COLLECTION = "users"
    }
}