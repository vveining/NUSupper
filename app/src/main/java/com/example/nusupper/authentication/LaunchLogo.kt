package com.example.nusupper.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.nusupper.FindJio
import com.example.nusupper.R
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser

class LaunchLogo : AppCompatActivity() {
    private lateinit var launchAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_logo)

        launchAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        //val user: FirebaseUser? = launchAuth.currentUser
        // ^ this statement should be used! set user to null so that users
        // have to login once they quit the app
        val user = null
        Toast.makeText(this,user.toString(),LENGTH_SHORT).show()
        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, FindJio::class.java))
        }
    }
}

