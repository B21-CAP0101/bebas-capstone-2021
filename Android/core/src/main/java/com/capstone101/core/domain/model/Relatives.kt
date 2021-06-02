package com.capstone101.core.domain.model

data class Relatives(
    val username: String,
    val invited: List<String>,
    val inviting: List<String>,
    val pure: List<String>
) {
    companion object {
        const val INVITING = "inviting"
        const val INVITED = "invited"
        const val PURE = "pure"
    }
}
