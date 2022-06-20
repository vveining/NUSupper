package com.example.nusupper.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.nusupper.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            if (checkFields()) {
                val email = binding.EmailAddress.text.toString()
                val password = binding.Password.text.toString()
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, Profile::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "invalid email and/or password!",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "empty fields are not allowed",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkFields(): Boolean {
        return (binding.EmailAddress.text.toString().trim{it<=' '}.isNotEmpty()
                && binding.Password.text.toString().trim{it<=' '}.isNotEmpty())
    }
}