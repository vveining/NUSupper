package com.example.nusupper.helpers

import com.example.nusupper.models.Food

interface ModifyFood {
    fun addFoodQty(foodName: String): Food
    fun addFoodQtyToMap(foodName: String): Food?
    fun refreshPage()
}