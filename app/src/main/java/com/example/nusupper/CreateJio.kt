package com.example.nusupper

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nusupper.databinding.ActivityCreateJioBinding
import com.example.nusupper.models.Jio
import com.example.nusupper.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

private const val TAG = "CreateJioActivity"
private const val EXTRA_USERNAME = "extra_username"

class CreateJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityCreateJioBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private var signedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        //navigation drawer things

        super.onCreate(savedInstanceState)
        binding = ActivityCreateJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                }
            }
            true
        }

        //create jio things

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

        // handle signed in user information
        // not rlly working tho
        firebaseDb = FirebaseFirestore.getInstance()
        var jiosReference = firebaseDb.collection("JIOS").limit(20)
        val username = intent.getStringExtra(EXTRA_USERNAME)
        if (username != null) {
            jiosReference = jiosReference.whereEqualTo("USERS.username", username)
        }

        firebaseDb.collection("USERS")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "failure fetching signed in user", exception)
            }

        // create jio button
        binding.createJioButton.setOnClickListener {
            handleCreateJioButtonClick()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleCreateJioButtonClick() {
        if (binding.locationAutocomplete.text.isBlank()) {
            Toast.makeText(this, "enter delivery location", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.restaurantAutocomplete.text.isBlank()) {
            Toast.makeText(this, "enter restaurant", Toast.LENGTH_SHORT).show()
            return
        }
//        if (signedInUser == null) {
//            Toast.makeText(this, "please sign in first", Toast.LENGTH_SHORT).show()
//            return
//        }

        // !PROBLEM : the app cant detect that user has signed in..................

        binding.createJioButton.isEnabled = false //to make asynchronous calls & enable it later

        //datePicker for when Jio object is fixed
        //can add timepicker as well
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val timePicker = findViewById<TimePicker>(R.id.timePicker1)

        val dd = datePicker.dayOfMonth
        val mm = datePicker.month
        val yyyy = datePicker.year
        val hour = timePicker.hour
        val min = timePicker.minute

        var t = LocalDateTime.of(yyyy,mm,dd,hour,min) //needs API 26

        val jio = Jio("2300", // not sure how to get user input for close time T_T
            t.toString(),
            signedInUser,
            binding.locationAutocomplete.text.toString(),
            true,
            binding.restaurantAutocomplete.text.toString())
        firebaseDb.collection("JIOS").add(jio).addOnCompleteListener { creation ->
            binding.createJioButton.isEnabled = true //enable button
            if (!creation.isSuccessful) {
                Log.e(TAG, "exception during firebase operations", creation.exception)
                Toast.makeText(this, "failed to create Jio", Toast.LENGTH_SHORT).show()
            }
            binding.locationAutocomplete.text.clear()
            binding.restaurantAutocomplete.text.clear()
            Toast.makeText(this, "created a Jio!", Toast.LENGTH_SHORT).show()

            // val intent = Intent(this, NEXT_PAGE::class.java)
            // startActivity(intent)
        }
    }

    //appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.createJioButton) {
//            val intent = Intent(this, FindJio::class.java)
//            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
//        }
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}