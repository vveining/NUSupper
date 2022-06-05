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
import com.google.android.material.navigation.NavigationView

class CreateJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityCreateJioBinding

    @RequiresApi(Build.VERSION_CODES.M)
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

            // Handle navigation view item clicks here.
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
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

        //create jio autocomplete dropdown
        val autoTextViewLocation = findViewById<AutoCompleteTextView>(R.id.locationAutocomplete)
        val locations = resources.getStringArray(R.array.residences_array)
        val adapter1 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, locations)
        autoTextViewLocation.setAdapter(adapter1)

        val autoTextViewRestaurant = findViewById<AutoCompleteTextView>(R.id.restaurantAutocomplete)
        val restaurantsArray = resources.getStringArray(R.array.restaurants_array)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurantsArray)
        autoTextViewRestaurant.setAdapter(adapter2)

        //retrieve text values of all fields when "let's jio is clicked"
        findViewById<Button>(R.id.letsJioBtn).setOnClickListener {
            val deliveryLocation = autoTextViewLocation.text.toString()
            val restaurant = autoTextViewRestaurant.text.toString()
            val closeDatePicker = findViewById<DatePicker>(R.id.jioDatePicker)
            val closeTimePicker = findViewById<TimePicker>(R.id.jioTimePicker)
            val closeDay = closeDatePicker.dayOfMonth // return type int
            val closeMonth = closeDatePicker.month
            val closeYear = closeDatePicker.year

            val closeHour = closeTimePicker.hour
            // !!!!!!! requires API 23 but minimum API for NUSUPPER is set at 21

            val closeMin = closeTimePicker.minute

            //intent to view created jio
            val intentStartViewJio = Intent(this,ViewJio::class.java)
            startActivity(intentStartViewJio)
        }

    }

    //appbar - toolbar button click
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