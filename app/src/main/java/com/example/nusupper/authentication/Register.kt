package com.example.nusupper.authentication

//import android.view.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nusupper.R
import com.example.nusupper.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User


class Register : AppCompatActivity() {

    // firebase auth variables
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseFirestore
    //checkbox click listener
    private var paylah = false
    private var paynow = false
    private var grabpay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase instance
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()

        //residence spinner in layout
        val resSpinner: Spinner = findViewById(R.id.residence_spinner)
        // create array adapter from residence array resources with simple spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.residences_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // use simple spinner layout view for dropdown
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // apply adapter to residence spinner
            resSpinner.adapter = adapter
        }


        //onclick listeners for payment checkbox
        binding.paylahCheck.setOnClickListener{
            onCheckboxClicked(it)
        }
        binding.grabpayCheck.setOnClickListener{
            onCheckboxClicked(it)
        }
        binding.paynowCheck.setOnClickListener{
            onCheckboxClicked(it)
        }

        //firebase data binding
        binding.registerWithData.setOnClickListener {
            if (checkFields()) {
                val name = binding.registerName.text.toString()
                val username = binding.registerUsername.text.toString()
                val email = binding.registerEmail.text.toString()
                val password = binding.registerPassword.text.toString()
                val mobilenumber = binding.registerPhone.text.toString()
                val residence = binding.residenceSpinner.selectedItem.toString()
                // val paylah =
                // val paynow =
                // val grabpay =
                val user = hashMapOf(
                    "name" to name,
                    "username" to username,
                    "email" to email,
                    "password" to password,
                    "mobile number" to mobilenumber,
                    "residence" to residence,
                    "paylah" to paylah,
                    "paynow" to paynow,
                    "grabpay" to grabpay
                )

                //creating user with firebase & validity check of user fields
                val users = firebaseDb.collection("USERS")
                users.whereEqualTo("email", email).get().addOnSuccessListener {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                users.document(email).set(user)
                                Toast.makeText(
                                    this,
                                    "successfully created account",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, Profile::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                try {
                                    throw it.exception!!
                                } catch (e: FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(
                                        this,
                                        "chosen password is too short (min 6 characters)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(
                                        this,
                                        "invalid email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: FirebaseAuthUserCollisionException) {
                                    Toast.makeText(
                                        this,
                                        "you already have an account with this email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this,
                                        it.exception.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                }
            } else {
                Toast.makeText(this, "empty fields are not allowed",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.paylahCheck -> {
                    paylah = checked
                }
                R.id.paynowCheck -> {
                    paynow = checked
                }
                R.id.grabpayCheck -> {
                    grabpay = checked
                }
            }
        }
    }

    // helper method for text field validation
    private fun checkFields(): Boolean {
        return (binding.registerName.text.toString().trim{it<=' '}.isNotEmpty()
                && binding.registerUsername.text.toString().trim{it<=' '}.isNotEmpty()
                && binding.registerEmail.text.toString().trim{it<=' '}.isNotEmpty()
                && binding.registerPassword.text.toString().trim{it<=' '}.isNotEmpty() &&
                (paylah || paynow || grabpay))
    }
}