package com.example.nusupper

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nusupper.databinding.ActivityViewFriendsProfileBinding
import com.example.nusupper.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ViewFriendsProfile: AppCompatActivity() {

    private lateinit var binding: ActivityViewFriendsProfileBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var jioID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFriendsProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialise variables
        jioID = intent.getStringExtra("EXTRA_JIOID").toString()

        // [START] friend's Profile things
        //retrieve profile info
        val email = intent.getStringExtra("friend's email")
        Toast.makeText(this,email,Toast.LENGTH_SHORT).show()

        if (email != null) {
            setText(email)
        } else {
            val intent = Intent(this, FindJio::class.java)
            startActivity(intent)
            finish()
        }

        // onclick for back button
        binding.backButton.setOnClickListener {
                Intent(this, ViewFriendsJio::class.java).also {
                    // send JIO ID info to viewJio activity to source for data
                    it.putExtra("EXTRA_JIOID", jioID)
                    startActivity(it)
                }
            }

        // [END] friend's Profile things
    }

    private fun setText(email: String?) {
        firebaseDb = FirebaseFirestore.getInstance()
        if (email != null) {
            firebaseDb.collection("USERS").document(email).get()
                .addOnSuccessListener {
                    val user = it.toObject<User>()
                    binding.name.text = it.get("name").toString()
                    binding.username.text = "@" + it.get("username").toString()
                    binding.mobileNumber.text = it.get("mobile number").toString()
                    val residence = it.get("residence").toString()
                    binding.residenceName.text = residence
                    binding.residenceImg.setImageResource(User.getResImg(residence))

                    if (user != null) {
                        val paylah: Boolean = user.paylah
                        val paynow = user.paynow
                        val grabpay = user.grabpay

                        if (paylah) {
                            binding.payment1.setImageResource(R.drawable.paylah)
                            if (paynow) {
                                binding.payment2.setImageResource(R.drawable.square_pn)
                                if (grabpay) {
                                    binding.payment3.setImageResource(R.drawable.grabpay)
                                }
                            } else if (grabpay) {
                                binding.payment2.setImageResource(R.drawable.grabpay)
                            }
                        } else if (paynow) { // no paylah
                            binding.payment1.setImageResource(R.drawable.square_pn)
                            if (grabpay) {
                                binding.payment2.setImageResource(R.drawable.grabpay)
                            }
                        } else if (grabpay) { // no paylah and no paynow
                            binding.payment1.setImageResource(R.drawable.grabpay)
                        }
                    }

                }
        }
    }
}