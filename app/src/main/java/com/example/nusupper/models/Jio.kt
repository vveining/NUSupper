package com.example.nusupper.models

import com.example.nusupper.R
import com.google.firebase.firestore.PropertyName

data class Jio(
    @get:PropertyName("close date") @set:PropertyName("close date") var closeDate: String = "",
    @get:PropertyName("close time") @set:PropertyName("close time") var closeTime: String = "",
    var creator: User? = null,
    var creatorEmail: String = "",
    var creatorUsername: String = "",
    var location: String = "",
    var open: Boolean = true,
    var restaurant: String = "",
    var jioID: String = "",
    var deliveryFee: Double = 0.0,
    var totalPrice: Double = 0.0,
    var foodArr: MutableList<Food> = mutableListOf(),
    var userFoodMap : HashMap<String, MutableList<Food>> = hashMapOf()) {

    companion object {
        fun getLogo(name: String): Int {
            when (name) {
                "SuperSnacks" -> return R.drawable.supersnacks
                "Starbucks" -> return R.drawable.starbucks
                "Fong Seng" -> return R.drawable.fongseng
                "McDonalds" -> return R.drawable.macs
                "Al-Amaan" -> return R.drawable.al_amaan
            }
            return R.drawable.food_icon
        }
    }

    fun addFood(food: Food, username: String): Jio { // add new food item
        val newArr = foodArr
        newArr.add(food)

        val newUserFoodMap = userFoodMap
        val usernameList = ArrayList(newUserFoodMap.keys)
        if (usernameList.contains(username)) {
            newUserFoodMap[username]?.add(food)
        } else {
            newUserFoodMap[username] = mutableListOf(food)
        }

        return Jio(this.closeDate, this.closeTime, this.creator,
            this.creatorEmail,this.creatorUsername,this.location,this.open,this.restaurant,
            this.jioID, this.deliveryFee, this.totalPrice, newArr, newUserFoodMap)
    }

    fun addNewFoodToMap(food: Food, username: String): Jio { // add new food item
        val newMap = userFoodMap
        var newArr = userFoodMap[username]
        val foodData = Food(
            food.foodName,
            1,
            food.price,
            food.price,
            food.remarks,
            food.username
        )
        if (newArr != null) { // if user already has a list
            newArr.add(foodData)
            newMap[username] = newArr
        } else { // if new user (no list)
            newArr = mutableListOf(foodData)
            newMap[username] = newArr
        }

        return Jio(this.closeDate, this.closeTime, this.creator,
            this.creatorEmail,this.creatorUsername,this.location,this.open,this.restaurant,
            this.jioID, this.deliveryFee, this.totalPrice, newArr, newMap)
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

    fun getFoodFromMap(foodName: String, username: String): Food? { // used to get the food object so that food obj can be modified
        var idx = -1
        val foodList = userFoodMap[username]
        if (foodList != null) {
            for (i in foodList.indices) {
                if (foodList[i].foodName == foodName) {
                    idx = i
                }
            }
        }
        return if (idx == -1) {
            null
        } else {
            foodList?.get(idx)
        }
    }

    fun updateFoodArr(food: Food): MutableList<Food> {
        for (i in foodArr.indices) {
            if (foodArr[i].foodName == food.foodName) {
                if (food.qty == 0) {
                    foodArr.removeAt(i)
                } else {
                    foodArr[i] = food
                }
                break
            }
        }
        return foodArr
    }

    fun updateUserFoodMap(food: Food, username: String): HashMap<String, MutableList<Food>> {
        val list = userFoodMap[username]
        if (list != null) {
            for (i in list.indices) {
                if (list[i].foodName == food.foodName) {
                    if (food.qty == 0) {
                        list.removeAt(i)
                    } else {
                        list[i] = food
                    }
                    userFoodMap.replace(username, list)
                    break
                }
            }
        }
        return userFoodMap
    }

    fun updateTotalPrice(): Jio {
        var newTotalPrice = this.deliveryFee
        for (i in foodArr.indices) {
            newTotalPrice += foodArr[i].totalPrice
        }
        return Jio(this.closeDate, this.closeTime, this.creator,
            this.creatorEmail,this.creatorUsername,this.location,this.open,this.restaurant,
            this.jioID, this.deliveryFee, newTotalPrice, this.foodArr, this.userFoodMap)
    }

    fun closeJio(): Jio {
        return Jio(closeDate, closeTime, creator, creatorEmail,
            creatorUsername, location, false, restaurant, jioID,
            deliveryFee, totalPrice, foodArr, userFoodMap)
    }

    //other functions to make
    // - counts total to be paid per person?

}