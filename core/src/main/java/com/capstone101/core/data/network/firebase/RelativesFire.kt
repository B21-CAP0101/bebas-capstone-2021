package com.capstone101.core.data.network.firebase

data class RelativesFire(
    val invited: List<String>,
    val inviting: List<String>,
    val pure: List<String>
) {
    companion object {
        const val COLLECTION = "relatives"
    }
}
