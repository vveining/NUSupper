package com.example.nusupper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nusupper.authentication.Profile
import com.example.nusupper.databinding.ActivityCreateJioBinding
import com.example.nusupper.models.Jio
import com.example.nusupper.models.UserAcc
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalTime
import java.util.*

class CreateJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityCreateJioBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var signedInUser: UserAcc? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [START navigation drawer things]

        val toolbar: Toolbar = findViewById(R.id.createjio_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.createjio_drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.createjio_nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // handle navigation view item clicks here
            when (menuItem.itemId) {

                R.id.profile -> {
                    Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                }
                R.id.createjio -> {
                    Toast.makeText(this, "create a Jio", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CreateJio::class.java)
                    startActivity(intent)
                }
                R.id.findjio -> {
                    Toast.makeText(this, "find a Jio", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, FindJio::class.java)
                    startActivity(intent)
                }
                R.id.orderhistory -> {
                    Toast.makeText(this, "order history", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,OrderHistory::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // [END navigation drawer things]

        // [START CreateJio things]

        // autocomplete for location and restaurant AutoCompleteTextView
        val autoTextViewLocation = findViewById<AutoCompleteTextView>(R.id.locationAutocomplete)
        val locations = resources.getStringArray(R.array.residences_array)
        val adapter1 = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, locations
        )
        autoTextViewLocation.setAdapter(adapter1)
        val autoTextViewRestaurant = findViewById<AutoCompleteTextView>(R.id.restaurantAutocomplete)
        val restaurants = resources.getStringArray(R.array.restaurants_array)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurants)
        autoTextViewRestaurant.setAdapter(adapter2)

        // get signed in user as a User object
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.email?.let {
            firebaseDb.collection("USERS")
                .document(it)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    signedInUser = userSnapshot.toObject(UserAcc::class.java)
                }
        }

        // create jio button; clicking it creates a Jio object
        binding.createJioButton.setOnClickListener {
            handleCreateJioButtonClick()
        }

        // [END CreateJio things]
    }

    // handles create jio button; returns a Jio object
    private fun handleCreateJioButtonClick() {

        // checks
        if (binding.locationAutocomplete.text.isBlank()) {
            Toast.makeText(this, "enter delivery location", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.restaurantAutocomplete.text.isBlank()) {
            Toast.makeText(this, "enter restaurant", Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser == null) {
            Toast.makeText(this, "please sign in first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.createJioButton.isEnabled = false // to make asynchronous calls & enable it later

        // format date string
        val dP = findViewById<DatePicker>(R.id.datePicker)
        val dateString = dateParse(dP.dayOfMonth,dP.month,dP.year)

        // to get closeTime from spinner
        val tP = findViewById<TimePicker>(R.id.timePicker1)
        val timeString = timeParse(tP.hour,tP.minute)

        //generate unique jio id
        val uniqueID = UUID.randomUUID().toString()

        // create a Jio object
        val jio = Jio(dateString,
            timeString,
            signedInUser,
            signedInUser!!.email,
            signedInUser!!.username,
            binding.locationAutocomplete.text.toString(),
            true,
            binding.restaurantAutocomplete.text.toString(),
            uniqueID,
            0.0,
            0.0,
            mutableListOf(), hashMapOf())

        // add the Jio object into firebase database
        firebaseDb.collection("JIOS").document(uniqueID).set(jio).addOnCompleteListener { creation ->
            binding.createJioButton.isEnabled = true //enable button
            if (!creation.isSuccessful) {
                Toast.makeText(this, "failed to create Jio", Toast.LENGTH_SHORT).show()
            }
            binding.locationAutocomplete.text.clear()
            binding.restaurantAutocomplete.text.clear()
            Toast.makeText(this, "created a Jio!", Toast.LENGTH_SHORT).show()

            Intent(this,ViewJio::class.java).also {
                //send JIO ID info to viewJio activity to source for data
                it.putExtra("EXTRA_JIOID",uniqueID)
                startActivity(it)
            }
        }
    }

    // appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object { // for testing purposes
        fun dateParse(dom: Int, monthInt: Int, year: Int): String {
            //settle datepicker -----------
            var month = (monthInt + 1).toString() //idk why but spinner takes the month before
            var domStr = dom.toString()

            //format 0s
            if (domStr.length == 1) {
                domStr = "0$domStr"
            }
            if (month.length == 1) {
                month = "0$month"
            }

            return "$domStr.$month.$year"
        }

        fun timeParse(hour: Int, min: Int): String {
            return LocalTime.of(hour, min).toString()
        }
    }
}