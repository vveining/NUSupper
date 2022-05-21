package com.example.nusupper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.button)
        loginButton.setOnClickListener {
            val toLogin = Intent(this, Profile::class.java)
            startActivity(toLogin)
        }
    }
}