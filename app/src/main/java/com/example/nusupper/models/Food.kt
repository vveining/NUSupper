package com.example.nusupper.models

import com.google.firebase.firestore.PropertyName

data class Food(
    var foodName: String = "",
    var qty: Int = 0,
    var price: Double = 0.0,
    var remarks: String = "",
    var username: String = "") {

    companion object Factory {
        fun createList(): Food = Food()
    }

    // !!!!!!!! NOTE: i made methods to change all attributes except the name of the person who created it

    fun chgName(newName: String): Food {
        return Food(newName,this.qty,this.price,this.remarks,this.username)
    }

    fun chgQty(newQty: Int): Food {
        return Food(this.foodName,newQty,this.price,this.remarks,this.username)
    }

    fun chg(newPrice: Double): Food {
        return Food(this.foodName,this.qty,newPrice,this.remarks,this.username)
    }

    fun chgRemarks(newRem: String): Food {
        return Food(this.foodName,this.qty,this.price,newRem,this.username)
    }

    //function ideas
    // - toggle function (ie press a + sign to increase qty of food ordered by 1 person

}