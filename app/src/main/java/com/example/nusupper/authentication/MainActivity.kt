package com.example.nusupper.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.nusupper.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val regButton = findViewById<Button>(R.id.registerButton)
        regButton.setOnClickListener {
            val toRegister = Intent(this, Register::class.java)
            startActivity(toRegister)
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val toLogin = Intent(this, Login::class.java)
            startActivity(toLogin)
        }
    }
}