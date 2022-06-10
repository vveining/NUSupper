package com.example.nusupper.models

import com.google.firebase.firestore.PropertyName

data class Jio(
    @get:PropertyName("close date") @set:PropertyName("close date") var closeDate: String = "",
    @get:PropertyName("close time") @set:PropertyName("close time") var closeTime: String = "",
    var creator: User? = null,
    var location: String = "",
    var open: Boolean = true,
    var restaurant: String = "")