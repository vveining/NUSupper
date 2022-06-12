package com.example.nusupper.models

import com.example.nusupper.R
import com.google.firebase.firestore.PropertyName

data class Jio(
    @get:PropertyName("close date") @set:PropertyName("close date") var closeDate: String = "",
    @get:PropertyName("close time") @set:PropertyName("close time") var closeTime: String = "",
    var creator: User? = null,
    var location: String = "",
    var open: Boolean = true,
    var restaurant: String = "",
    var jioID: String = "") {

    companion object {
        fun getLogo(name: String): Int {
            when (name) {
                "Super Snacks" -> return R.drawable.supersnacks
                "Starbucks" -> return R.drawable.starbucks
                "Fong Seng" -> return R.drawable.fongseng
                "McDonalds" -> return R.drawable.macs
                "Al-Amaan" -> return R.drawable.al_amaan
            }
            return R.drawable.food_icon
        }
    }

}