package com.example.nusupper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.nusupper.databinding.ActivityEditJioBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalTime

class EditJio : AppCompatActivity() {

    private lateinit var binding: ActivityEditJioBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var jioID: String
    private lateinit var location: String
    private lateinit var restaurant: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityEditJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [START EditJio things]

        jioID = intent.getStringExtra("EXTRA_JIOID").toString()

        setEditJio(jioID)

        // autocomplete for location and restaurant AutoCompleteTextView
        val autoTextViewLocation = findViewById<AutoCompleteTextView>(R.id.editJiolocationAutocomplete)
        val locations = resources.getStringArray(R.array.residences_array)
        val adapter1 = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, locations
        )
        autoTextViewLocation.setAdapter(adapter1)
        val autoTextViewRestaurant = findViewById<AutoCompleteTextView>(R.id.editJiorestaurantAutocomplete)
        val restaurants = resources.getStringArray(R.array.restaurants_array)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurants)
        autoTextViewRestaurant.setAdapter(adapter2)

        // handle done button
        binding.editJioButton.setOnClickListener {
            handleEditJioButtonClick()
        }

        // [END EditJio things]
    }

    private fun handleEditJioButtonClick() {
        binding.editJioButton.isEnabled = false // to make asynchronous calls & enable it later

        // get updated (if any) location & restaurant
        var editJioLocation = location
        val updatedLocation = binding.editJiolocationAutocomplete.text.toString()
        if (updatedLocation != "") {
            editJioLocation = updatedLocation
        }
        var editJioRestaurant = restaurant
        val updatedRestaurant = binding.editJiorestaurantAutocomplete.text.toString()
        if (updatedRestaurant != "") {
            editJioRestaurant = updatedRestaurant
        }

        // to get closeDate from spinner
        val datePicker = findViewById<DatePicker>(R.id.editJio_datePicker)
        val dateString = datePicker.dayOfMonth.toString() + "." + datePicker.month.toString() +
                "." + datePicker.year.toString()

        // to get closeTime from spinner
        val timePicker = findViewById<TimePicker>(R.id.editJio_timePicker1)
        val hour = timePicker.hour
        val min = timePicker.minute
        val timeString = LocalTime.of(hour,min).toString()

        firebaseDb = FirebaseFirestore.getInstance()
        firebaseDb.collection("JIOS").document(jioID).update(
            "location", editJioLocation,
            "restaurant", editJioRestaurant,
            "close date", dateString,
            "close time", timeString)

        Toast.makeText(this, "Jio updated", Toast.LENGTH_SHORT).show()

        Intent(this,ViewJio::class.java).also {
            //send JIO ID info to viewJio activity to source for data
            it.putExtra("EXTRA_JIOID",jioID)
            startActivity(it)
        }
    }

    private fun setEditJio(jioID: String?) {
        val firebaseDb = FirebaseFirestore.getInstance()
        if (jioID != null) {
            firebaseDb.collection("JIOS").document(jioID).get()
                .addOnSuccessListener {
                    val initialLocation = it.get("location").toString()
                    location = initialLocation
                    binding.editJiolocationAutocomplete.hint = initialLocation
                    val initialRestaurant = it.get("restaurant").toString()
                    restaurant = initialRestaurant
                    binding.editJiorestaurantAutocomplete.hint = initialRestaurant
                }
        }
    }
}