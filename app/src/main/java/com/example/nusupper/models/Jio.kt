package com.example.nusupper.models
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime

data class Jio(
    @get:PropertyName("close time") @set:PropertyName("close time") var closeTime: String,
    // ik close time is supposed to be a number but it will be a string for now
    var closeDT: String,
    var creator: User? = null,
    var location: String = "",
    var open: Boolean = true,
    var restaurant: String = "")