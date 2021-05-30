package com.capstone101.core.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Danger(
    var id: String? = null,
    var place: GeoPoint? = null,
    var record: String? = null,
    var time: Timestamp? = null
)
