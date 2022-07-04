package com.example.nusupper.helpers

import com.example.nusupper.models.Food

interface PaymentHelpers {
    fun getUsersTotalBill(username: String, userFoodList: HashMap<String, MutableList<Food>>): Double
}