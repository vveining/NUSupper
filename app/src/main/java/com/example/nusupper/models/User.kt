package com.example.nusupper.models

import com.example.nusupper.R
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
    var grabpay: Boolean = false) {

    companion object {
        fun getResImg(residence: String): Int {
            when (residence) {
                "CAPT" -> return R.drawable.capt
                "RC4" -> return R.drawable.rc4
                "Tembusu" -> return R.drawable.tembusu
                "RVRC" -> return R.drawable.rvrc
                "Cinnamon" -> return R.drawable.cinnamon
                "Kent Ridge Hall" -> return R.drawable.kentridge
                "Sheares Hall" -> return R.drawable.sheares
                "Eusoff Hall" -> return R.drawable.eusoff
                "Temasek Hall" -> return R.drawable.temasek
                "Raffles Hall" -> return R.drawable.raffles
                "King Edward VII Hall" -> return R.drawable.ke7
                "PGPR" -> return R.drawable.pgpr
            }
            return R.drawable.capt
        }
    }
}