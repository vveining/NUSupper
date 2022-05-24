package com.example.nusupper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.nusupper.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()

        binding.registerWithData.setOnClickListener {
            if (checkFields()) {
                val name = binding.registerName.text.toString()
                val username = binding.registerUsername.text.toString()
                val email = binding.registerEmail.text.toString()
                val password = binding.registerPassword.text.toString()
                val user = hashMapOf(
                    "name" to name,
                    "username" to username,
                    "email" to email,
                    "password" to password
                )
                val users = firebaseDb.collection("USERS")
                users.whereEqualTo("email", email).get().addOnSuccessListener {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                users.document(email).set(user)
                                val intent = Intent(this, Profile::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, it.exception.toString(),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "empty fields are not allowed",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkFields(): Boolean {
        return (binding.registerName.text.toString().trim{it<=' '}.isNotEmpty()
                && binding.registerUsername.text.toString().trim{it<=' '}.isNotEmpty()
                && binding.registerEmail.text.toString().trim{it<=' '}.isNotEmpty()
                && binding.registerPassword.text.toString().trim{it<=' '}.isNotEmpty())
    }
}