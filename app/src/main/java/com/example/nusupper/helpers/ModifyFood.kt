package com.example.nusupper.helpers

import com.example.nusupper.models.Food

interface ModifyFood {
    fun addFoodQtyToMap(foodName: String): Food?
    fun removeFoodQtyToMap(foodName: String): Food?
    fun checkIfFoodPresent(foodName: String): Boolean
    fun refreshPage()
}