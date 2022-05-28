package com.example.nusupper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nusupper.databinding.ActivityProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val isLogin = sharedPref.getString("email", "1")

        //handle logout
        binding.logoutButton.setOnClickListener {
            sharedPref.edit().remove("email").apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (isLogin == "1") {
            val email = intent.getStringExtra("email")
            if (email != null) {
                setText(email)
                with(sharedPref.edit()) {
                    putString("email", email)
                    apply()
                }
            }
            else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            setText(isLogin)
        }
    }

    private fun setText(email: String?) {
        firebaseDb = FirebaseFirestore.getInstance()
        if (email != null) {
            firebaseDb.collection("USERS").document(email).get()
                .addOnSuccessListener {
                    binding.name.text = it.get("name").toString()
                    binding.username.text = it.get("username").toString()
                    binding.mobileNumber.text = it.get("mobile number").toString()
                    binding.residenceName.text = it.get("residence").toString()
                }
        }
    }
}