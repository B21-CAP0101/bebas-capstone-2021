package com.capstone101.core.data.network.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class DangerFire(
    val id: String?,
    var place: GeoPoint? = null,
    var record: String? = null,
    var time: Timestamp? = null
) {
    companion object {
        const val COLLECTION = "danger"
        const val SUB_COLLECTION = "record"
    }
}