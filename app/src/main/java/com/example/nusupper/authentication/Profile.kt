package com.example.nusupper.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nusupper.CreateJio
import com.example.nusupper.FindJio
import com.example.nusupper.OrderHistory
import com.example.nusupper.R
import com.example.nusupper.databinding.ActivityProfileBinding
import com.example.nusupper.models.UserAcc
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class Profile : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // navigation drawer things <start>

        val toolbar: Toolbar = findViewById(R.id.profile_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.profile_drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.profile_nav_view)
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
                    val intent = Intent(this, OrderHistory::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // navigation drawer things <end>

        // Profile things <start>

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val isLogin = sharedPref.getString("email", "1")

        //handle logout
        firebaseAuth = FirebaseAuth.getInstance()
        binding.logoutButton.setOnClickListener {
            sharedPref.edit().remove("email").apply()
            val intent = Intent(this, MainActivity::class.java)
            firebaseAuth.signOut()
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
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            setText(isLogin)
        }

        // Profile things <end>
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
                    val user = it.toObject<UserAcc>()
                    binding.name.text = it.get("name").toString()
                    binding.username.text = "@" + it.get("username").toString()
                    binding.mobileNumber.text = it.get("mobile number").toString()
                    val residence = it.get("residence").toString()
                    binding.residenceName.text = residence
                    binding.residenceImg.setImageResource(UserAcc.getResImg(residence))

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