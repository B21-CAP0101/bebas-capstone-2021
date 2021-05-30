package com.capstone101.core.data.network.firebase

data class RelativesFire(
    var invited: List<String>? = null,
    var inviting: List<String>? = null,
    var pure: List<String>? = null
) {
    companion object {
        const val COLLECTION = "relatives"
    }
}
