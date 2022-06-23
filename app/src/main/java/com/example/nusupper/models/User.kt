package com.example.nusupper.models

import com.google.firebase.firestore.PropertyName

data class User(
    var email: String = "",
    @get:PropertyName("mobile number") @set:PropertyName("mobile number") var mobileNumber: String = "",
    var name: String = "",
    var password: String = "",
    var residence: String = "",
    var username: String = "",
    var paylah: Boolean = false,
    var paynow: Boolean = false,
    var grabpay: Boolean = false)