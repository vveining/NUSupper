package com.example.nusupper

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJioOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialise variables
        jioID = intent.getStringExtra("EXTRA_JIOID").toString()

        // [START JioOrders things]

        // data source always updates
        foods = mutableListOf()

        // create adapter for foods
        adapter = FoodsAdapter(this, foods)

        // bind adapter to listviews
        compiledorders_listview.adapter = adapter

        // onclick for return to back page
        binding.backButton.setOnClickListener {
            Intent(this, ViewFriendsJio::class.java).also {
                // send JIO ID info to viewJio activity to source for data
                it.putExtra("EXTRA_JIOID", jioID)
                startActivity(it)
            }
        }

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

        // get Jio object from firebaseDb
        firebaseDb.collection("JIOS").document(jioID).get()
            .addOnSuccessListener {
                thisJio = it.toObject<Jio>()!!

                foods.clear()
                addFoodToList(it)
            }

        // create an alertDialog for user to input food details
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

                // add Food object to thisJio
                thisJio = thisJio.addFood(newFood)
                firebaseDb.collection("JIOS").document(jioID).update(
                    "foodArr", thisJio.foodArr
                )
                Toast.makeText(this, "new order added", Toast.LENGTH_SHORT).show()

                // refresh activity
                finish();
                startActivity(intent);

            }

            alertDialog.show()
        }

        // [END JioOrders things]
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
    }

    override fun addFoodQty(foodName: String): Food {
        var thisFood = thisJio.getFood(foodName)
        thisFood = thisFood.addQty()
        updateFirebase(thisFood)
        return thisFood
    }

    fun updateFirebase(food: Food) {
        val updatedFoodArr = thisJio.updateFoodArr(food)
        firebaseDb.collection("JIOS").document(jioID).update(
            "foodArr", updatedFoodArr
        )
    }
}