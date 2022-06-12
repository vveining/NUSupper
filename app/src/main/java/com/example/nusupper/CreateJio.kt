package com.example.nusupper

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalTime

private const val EXTRA_USERNAME = "extra_username"

class CreateJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityCreateJioBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var signedInUser: User? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // navigation drawer things <start>

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

        // navigation drawer things <end>

        // CreateJio things <start>

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
                    signedInUser = userSnapshot.toObject(User::class.java)
                }
        }

        // create jio button; clicking it creates a Jio object
        binding.createJioButton.setOnClickListener {
            handleCreateJioButtonClick()
        }

        // CreateJio things <end>
    }

    // handles create jio button; returns a Jio object
    @RequiresApi(Build.VERSION_CODES.O) // for timePicker to work without errors
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

        // to get closeDate from spinner
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val dateString = datePicker.dayOfMonth.toString() + "." + datePicker.month.toString() +
                "." + datePicker.year.toString()

        // to get closeTime from spinner
        val timePicker = findViewById<TimePicker>(R.id.timePicker1)
        val hour = timePicker.hour
        val min = timePicker.minute
        val timeString = LocalTime.of(hour,min).toString()

        // create a Jio object
        val jio = Jio(dateString,
            timeString,
            signedInUser,
            binding.locationAutocomplete.text.toString(),
            true,
            binding.restaurantAutocomplete.text.toString())

        // add the Jio object into firebase database
        firebaseDb.collection("JIOS").add(jio).addOnCompleteListener { creation ->
            binding.createJioButton.isEnabled = true //enable button
            if (!creation.isSuccessful) {
                Toast.makeText(this, "failed to create Jio", Toast.LENGTH_SHORT).show()
            }
            binding.locationAutocomplete.text.clear()
            binding.restaurantAutocomplete.text.clear()
            Toast.makeText(this, "created a Jio!", Toast.LENGTH_SHORT).show()

            // val intent = Intent(this, NEXT_PAGE::class.java)
            // startActivity(intent)
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
}