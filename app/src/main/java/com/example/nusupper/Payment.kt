package com.example.nusupper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nusupper.adapters.PaymentBillAdapter
import com.example.nusupper.adapters.PaymentFoodAdapter
import com.example.nusupper.databinding.ActivityPaymentBinding
import com.example.nusupper.helpers.PaymentHelpers
import com.example.nusupper.models.Food
import com.example.nusupper.models.Jio
import com.example.nusupper.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_payment.*

class Payment : AppCompatActivity(), PaymentHelpers {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var jioID: String
    private lateinit var thisJio: Jio
    private var signedInUser: User? = null
    private var firebaseDb = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var foods: MutableList<Food>
    private lateinit var compiledAdapter: PaymentFoodAdapter
    private lateinit var billsAdapter: PaymentBillAdapter
    private lateinit var usernameList: List<String>
    private lateinit var userFoods: HashMap<String, MutableList<Food>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialise variables
        jioID = intent.getStringExtra("EXTRA_JIOID").toString()

        // [START] payment page things

        // initialise lists and adapter(s)
        initialiseListsAndAdapters()

        // set onclick functions for button(s)
        setButtons()

        // get signed in user as a User object
        getSignedInUser()

        // get Jio object from firebaseDb
        firebaseDb.collection("JIOS").document(jioID).get()
            .addOnSuccessListener {
                thisJio = it.toObject<Jio>()!!

                // display the relevant texts in the layout
                setText(thisJio)

                // update lists with most recent data and update adapter(s)
                updateAdapters(it)
            }

        // [END] payment page things

    }

    private fun getSignedInUser() {
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
            finish()
            startActivity(intent)
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

    }

    private fun initialiseListsAndAdapters() {
        // data source always updates
        foods = mutableListOf()
        userFoods = hashMapOf()

        // create adapter for foods and userFoods
        compiledAdapter = PaymentFoodAdapter(this, foods)
        usernameList = ArrayList(userFoods.keys)
        billsAdapter = PaymentBillAdapter(this, usernameList as ArrayList<String>, userFoods)

        // bind adapter to listviews
        payment_compiledorders_listview.adapter = compiledAdapter
        bills_listView.adapter = billsAdapter
    }

    private fun updateAdapters(it: DocumentSnapshot?) {
        // update foods from firebase
        foods.clear()
        addFoodToList(it)

        // update userFoods from firebase
        userFoods.clear()
        addUserFoodsToMap(it)

        // bind billsAdapter to listView
        usernameList = ArrayList(userFoods.keys)
        billsAdapter = PaymentBillAdapter(this, usernameList as ArrayList<String>, userFoods)
        bills_listView.adapter = billsAdapter
    }

    private fun setText(thisJio: Jio) {
        // display jio owner's username
        binding.jioOwnerStub.text = thisJio.creator?.username

        // display deliveryFee
        binding.deliverypriceStub.text = thisJio.deliveryFee.toString()

        // display total price
        binding.totalPriceStub.text = thisJio.updateTotalPrice().totalPrice.toString()

        // display creator's preferred payment methods
        binding.billsInfo1.text = thisJio.creator?.username
        if (thisJio.creator?.grabpay == true) {
            binding.billsInfoGrabPay.text = "GRABPAY"
        }

        if (thisJio.creator?.paylah == true) {
            binding.billsInfoPaylah.text = "PAYLAH!"
        }

        if (thisJio.creator?.paynow == true) {
            binding.billsInfoPaynow.text = "PAYNOW"
        }
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
        compiledAdapter.notifyDataSetChanged()
        billsAdapter.notifyDataSetChanged()
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
        compiledAdapter.notifyDataSetChanged()
        billsAdapter.notifyDataSetChanged()
    }

    override fun getUsersTotalBill(username: String, userFoodList: HashMap<String, MutableList<Food>>): Double {
        var totalBill: Double

        val numOfPeople = userFoodList.keys.size
        totalBill = thisJio.deliveryFee / numOfPeople

        val list = userFoodList[username]
        if (list != null) {
            for (i in list.indices) {
                totalBill += list[i].totalPrice
            }
        }
        return totalBill
    }
}