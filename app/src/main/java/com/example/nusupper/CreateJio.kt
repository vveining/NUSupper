package com.example.nusupper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class CreateJio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_jio)
        val autoTextViewLocation = findViewById<AutoCompleteTextView>(R.id.locationAutocomplete)
        val locations = resources.getStringArray(R.array.residences_array)
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, locations)
        autoTextViewLocation.setAdapter(adapter)
    }
}