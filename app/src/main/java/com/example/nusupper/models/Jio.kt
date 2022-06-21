package com.example.nusupper.models

import com.example.nusupper.R
import com.google.firebase.firestore.PropertyName

data class Jio(
    @get:PropertyName("close date") @set:PropertyName("close date") var closeDate: String = "",
    @get:PropertyName("close time") @set:PropertyName("close time") var closeTime: String = "",
    var creator: User? = null,
    var creatorEmail: String = "",
    var location: String = "",
    var open: Boolean = true,
    var restaurant: String = "",
    var jioID: String = "",
    var foodArr: MutableList<Food> = mutableListOf()) {

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

    fun addFood(food: Food): Jio { //add new food item
        val newArr = foodArr
        newArr.add(food)
        return Jio(this.closeDate, this.closeTime, this.creator,
            this.creatorEmail,this.location,this.open,this.restaurant,
            this.jioID, newArr)
    }

    fun removeFood(idx: Int): Jio { //remove food item by index, can utilise for recycler view
        val newArr = foodArr
        newArr.removeAt(idx)
        return Jio(this.closeDate, this.closeTime, this.creator,
            this.creatorEmail,this.location,this.open,this.restaurant,
            this.jioID, newArr)
    }

    fun getFood(foodName: String): Food { // used to get the food object so that food obj can be modified
        var idx = 0
        for (i in foodArr.indices) {
            if (foodArr[i].foodName == foodName) {
                idx = i
            }
        }
        return foodArr[idx]
    }

    fun updateFoodArr(food: Food): MutableList<Food> {
        for (i in foodArr.indices) {
            if (foodArr[i].foodName == food.foodName) {
                foodArr[i] = food
                break
            }
        }
        return foodArr
    }

    //other functions to make
    // - counts total to be paid per person?

}