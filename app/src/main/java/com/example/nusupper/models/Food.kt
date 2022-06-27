package com.example.nusupper.models

import com.google.firebase.firestore.PropertyName

data class Food(
    var foodName: String = "",
    var qty: Int = 0,
    var price: Double = 0.0,
    var totalPrice: Double = 0.0,
    var remarks: String = "",
    var username: String = "") {

    fun addQty(): Food {
        val updatedQty = this.qty + 1
        val updatedTotalPrice = this.price * updatedQty
        this.qty = updatedQty // mutable list
        this.totalPrice = updatedTotalPrice // mutable list
        return this
    }

    fun removeQty(): Food {
        val updatedQty = this.qty - 1
        val updatedTotalPrice = this.price * updatedQty
        this.qty = updatedQty // mutable list
        this.totalPrice = updatedTotalPrice // mutable list
        return this
    }

    //function ideas
    // - toggle function (ie press a + sign to increase qty of food ordered by 1 person

}