package com.capstone101.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Relatives(
    val username: String,
    val invited: List<String>,
    val inviting: List<String>,
    val pure: List<String>
) : Parcelable {
    companion object {
        const val INVITING = "inviting"
        const val INVITED = "invited"
        const val PURE = "pure"
    }
}
