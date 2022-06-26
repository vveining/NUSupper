package com.example.nusupper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nusupper.authentication.Profile
import com.example.nusupper.databinding.ActivityViewFriendsProfileBinding
import com.example.nusupper.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ViewFriendsProfile: AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityViewFriendsProfileBinding
    private lateinit var firebaseDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFriendsProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // navigation drawer things <start>

        val toolbar: Toolbar = findViewById(R.id.fprofile_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.fprofile_drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.fprofile_nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // handle navigation view item clicks here
            when (menuItem.itemId) {

                R.id.profile -> {
                    Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                }
                R.id.createjio -> {
                    Toast.makeText(this, "create a Jio", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CreateJio::class.java)
                    startActivity(intent)
                }
                R.id.findjio -> {
                    Toast.makeText(this, "find a Jio", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, FindJio::class.java)
                    startActivity(intent)
                }
                R.id.orderhistory -> {
                    Toast.makeText(this, "order history", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        // navigation drawer things <end>

        // friend's Profile things <start>
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

        // friend's Profile things <end>
    }

    // appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setText(email: String?) {
        firebaseDb = FirebaseFirestore.getInstance()
        if (email != null) {
            firebaseDb.collection("USERS").document(email).get()
                .addOnSuccessListener {
                    var user = it.toObject<User>()
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