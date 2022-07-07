package com.example.nusupper

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nusupper.adapters.FoodsAdapter
import com.example.nusupper.adapters.OrdersAdapter
import com.example.nusupper.databinding.ActivityJioOrdersBinding
import com.example.nusupper.helpers.ModifyFood
import com.example.nusupper.models.Food
import com.example.nusupper.models.Jio
import com.example.nusupper.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_jio_orders.*
import kotlinx.android.synthetic.main.add_order_alertdialog.*
import kotlinx.android.synthetic.main.add_order_alertdialog.view.*

class JioOrders : AppCompatActivity(), ModifyFood {

    private lateinit var binding: ActivityJioOrdersBinding
    private lateinit var jioID: String
    private var firebaseDb = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var foods: MutableList<Food>
    private lateinit var adapter: FoodsAdapter
    private lateinit var thisJio: Jio
    private var signedInUser: User? = null
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var usernameList: List<String>
    private lateinit var userFoods: HashMap<String, MutableList<Food>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJioOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialise variables
        jioID = intent.getStringExtra("EXTRA_JIOID").toString()

        // [START JioOrders things]

        // initialise lists and adapter(s)
        initialiseListsAndAdapters()

        // get signed in user as a User object
        getSignedInUser()

        // get Jio object from firebaseDb
        firebaseDb.collection("JIOS").document(jioID).get()
            .addOnSuccessListener {
                thisJio = it.toObject<Jio>()!!

                // display the relevant texts in the layout
                setText(thisJio)

                // set onclick functions for button(s)
                setButtons()

                // update lists with most recent data and update adapter(s)
                updateAdapters(it)
            }

        // create an alertDialog for user to input food details
        alertDialogHelper()

        // [END JioOrders things]
    }

    private fun alertDialogHelper() {
        binding.newOrderButton.setOnClickListener {

            // build and show alertdialog popup
            val dialogView = LayoutInflater.from(this).inflate(R.layout.add_order_alertdialog, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("New Order")
            val alertDialog = dialogBuilder.show()

            alertDialog.addButton.setOnClickListener {

                // get user input from editText boxes
                val foodName = dialogView.food_name.text.toString()
                val quantityText = dialogView.quantity.text.toString()
                val quantity = quantityText.toInt() // assume type is correct for now
                val priceText = dialogView.price.text.toString()
                val price = priceText.toDouble() // assume type is correct for now
                val totalPrice = price * quantity
                val remarks = dialogView.remarks.text.toString()

                // create a new Food object
                val newFood = Food(
                    foodName,
                    quantity,
                    price,
                    totalPrice,
                    remarks,
                    signedInUser!!.username)

                // dismiss popup
                alertDialog.dismiss()

                // add Food object to thisJio (in both foodArr and userFoodArr)
                thisJio = thisJio.addFood(newFood, signedInUser!!.username)
                firebaseDb.collection("JIOS").document(jioID).update(
                    "foodArr", thisJio.foodArr,
                    "userFoodMap", thisJio.userFoodMap
                )
                Toast.makeText(this, "new order added", Toast.LENGTH_SHORT).show()

                // refresh activity
                finish();
                startActivity(intent);

            }

            alertDialog.show()
        }
    }

    private fun updateAdapters(it: DocumentSnapshot?) {
        // update foods from firebase
        foods.clear()
        addFoodToList(it)

        // update userFoods from firebase
        userFoods.clear()
        addUserFoodsToMap(it)

        // bind ordersAdapter to expandableListView
        usernameList = ArrayList(userFoods.keys)
        ordersAdapter = OrdersAdapter(this, usernameList, userFoods)
        everyonesOrders_expandablelistview!!.setAdapter(ordersAdapter)

        // expand all groups by default
        for (i in 0 until ordersAdapter.groupCount) {
            everyonesOrders_expandablelistview.expandGroup(i)
        }
    }

    private fun setText(thisJio: Jio) {
        // display jio owner's username
        binding.jioOwnerStub.text = thisJio.creator?.username
    }

    private fun getSignedInUser() {
        // get signed in user as a User object
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.email?.let {
            firebaseDb.collection("USERS")
                .document(it)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    signedInUser = userSnapshot.toObject(User::class.java)
                }
        }
    }

    private fun setButtons() {
        // onclick for refresh page
        binding.refreshButton.setOnClickListener {
            // refresh activity
            finish();
            startActivity(intent);
        }

        // onclick for back button
        binding.backButton.setOnClickListener {
            if (thisJio.creatorEmail == signedInUser?.email) {
                Intent(this, ViewJio::class.java).also {
                    // send JIO ID info to viewJio activity to source for data
                    it.putExtra("EXTRA_JIOID", jioID)
                    startActivity(it)
                }
            } else {
                Intent(this, ViewFriendsJio::class.java).also {
                    // send JIO ID info to viewJio activity to source for data
                    it.putExtra("EXTRA_JIOID", jioID)
                    startActivity(it)
                }
            }
        }

        // onclick for updateYourOrder button
        binding.updateOrderButton.setOnClickListener {
            if (thisJio.creatorEmail == signedInUser?.email) {
                Intent(this, ViewJio::class.java).also {
                    // send JIO ID info to viewJio activity to source for data
                    it.putExtra("EXTRA_JIOID", jioID)
                    startActivity(it)
                }
            } else {
                Intent(this, ViewFriendsJio::class.java).also {
                    // send JIO ID info to viewJio activity to source for data
                    it.putExtra("EXTRA_JIOID", jioID)
                    startActivity(it)
                }
            }
        }

        // update firebaseDb with delivery fee
        binding.updateDeliveryFeeButton.setOnClickListener {
            val deliveryFee: Double
            val text = binding.deliveryfeeEdittext.text.toString()
            if (text.isEmpty()) {
                deliveryFee = 0.0
            } else {
                deliveryFee = text.toDouble()
            }
            firebaseDb.collection("JIOS").document(jioID).update(
                "deliveryFee", deliveryFee
            )
            Toast.makeText(this, "delivery fee updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initialiseListsAndAdapters() {
        // data source always updates
        foods = mutableListOf()
        userFoods = hashMapOf()

        // create adapter for foods and userFoods
        adapter = FoodsAdapter(this, foods)
        usernameList = ArrayList(userFoods.keys)
        ordersAdapter = OrdersAdapter(this, usernameList, userFoods)

        // bind adapter to listviews
        compiledorders_listview.adapter = adapter
        everyonesOrders_expandablelistview!!.setAdapter(ordersAdapter)

    }

    private fun addFoodToList(snapshot: DocumentSnapshot?) {
        for (i in thisJio.foodArr.listIterator()) {
            val foodData = Food(
                i.foodName,
                i.qty,
                i.price,
                i.totalPrice,
                i.remarks,
                i.username
            )
            foods.add(foodData)
        }
        adapter.notifyDataSetChanged()
        ordersAdapter.notifyDataSetChanged()
    }

    private fun addUserFoodsToMap(snapshot: DocumentSnapshot?) {
        val hashMap = thisJio.userFoodMap
        for (key in hashMap.keys) {
            val mutableList = mutableListOf<Food>()
            for (i in hashMap[key]!!.listIterator()) {
                val foodData = Food(
                    i.foodName,
                    i.qty,
                    i.price,
                    i.totalPrice,
                    i.remarks,
                    i.username
                )
                mutableList.add(foodData)
            }
            userFoods[key] = mutableList
        }
        adapter.notifyDataSetChanged()
        ordersAdapter.notifyDataSetChanged()
    }

    override fun addFoodQtyToMap(foodName: String): Food? {
        var thisFood = thisJio.getFoodFromMap(foodName, signedInUser!!.username)
        var foodFromFoodArr = thisJio.getFood(foodName)

        if (thisFood != null) { // food is already in user's array
            thisFood = thisFood.addQty()
            foodFromFoodArr = foodFromFoodArr.addQty()  // when adding to userFoodMap must also add to compiled foodArr list
        } else { // food is not in user's array yet
            signedInUser?.username?.let { thisJio.addNewFoodToMap(foodFromFoodArr, it) }
            thisFood = thisJio.getFoodFromMap(foodName, signedInUser!!.username)
            foodFromFoodArr = foodFromFoodArr.addQty()
        }

        if (thisFood != null) {
            updateFirebaseForMap(thisFood)
        }
        updateFirebase(foodFromFoodArr)

        return thisFood
    }

    override fun removeFoodQtyToMap(foodName: String): Food? {
        var thisFood = thisJio.getFoodFromMap(foodName, signedInUser!!.username)
        var foodFromFoodArr = thisJio.getFood(foodName)

        // if food is in user's array --> user has the right to remove qty;
        // else if food is NOT in user's array --> user cannot remove qty.
        if (thisFood != null) {
            thisFood = thisFood.removeQty()
            // when removing qty from userFoodMap, must also remove from compiled foodArr list
            foodFromFoodArr = foodFromFoodArr.removeQty()
        }

        if (thisFood != null) {
            updateFirebaseForMap(thisFood)
        }
        updateFirebase(foodFromFoodArr)

        return thisFood
    }

    override fun checkIfFoodPresent(foodName: String): Boolean {
        val thisFood = thisJio.getFoodFromMap(foodName, signedInUser!!.username)
        return if (thisFood == null) {
            Toast.makeText(this, "you do not have this order", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun updateFirebase(food: Food) {
        val updatedFoodArr = thisJio.updateFoodArr(food)
        firebaseDb.collection("JIOS").document(jioID).update(
            "foodArr", updatedFoodArr
        )
    }

    private fun updateFirebaseForMap(food: Food) {
        val updatedUserFoodMap = thisJio.updateUserFoodMap(food, signedInUser!!.username)
        firebaseDb.collection("JIOS").document(jioID).update(
            "userFoodMap", updatedUserFoodMap
        )
    }

    override fun refreshPage() {
        // refresh activity
        finish();
        startActivity(intent);
    }
}