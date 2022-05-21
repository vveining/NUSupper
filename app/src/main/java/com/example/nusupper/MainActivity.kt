package com.example.nusupper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val regButton = findViewById<Button>(R.id.registerButton)
        regButton.setOnClickListener {
            val toRegister = Intent(this,Register::class.java)
            startActivity(toRegister)
        }
    }
}