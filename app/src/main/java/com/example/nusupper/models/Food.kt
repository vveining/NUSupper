package com.example.nusupper.models

import com.google.firebase.firestore.PropertyName

data class Food(
    var foodName: String = "",
    var qty: Int = 0,
    var remarks: String = "",
    var username: String = "")