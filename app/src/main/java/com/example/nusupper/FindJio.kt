package com.example.nusupper

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
import com.example.nusupper.databinding.ActivityFindJioBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class FindJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityFindJioBinding
    private lateinit var firebaseDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.findjio_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.findjio_drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.findjio_nav_view)
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

        // clicking on individual jio buttons
        binding.jio1.setOnClickListener {
            Toast.makeText(this, "jio 1", Toast.LENGTH_SHORT).show()
        }

        binding.jio2.setOnClickListener {
            Toast.makeText(this, "jio 2", Toast.LENGTH_SHORT).show()
        }

        binding.addjioBox.setOnClickListener {
            Toast.makeText(this, "add jio", Toast.LENGTH_SHORT).show()
        }

        binding.communityJio1.setOnClickListener {
            Toast.makeText(this, "community jio 1", Toast.LENGTH_SHORT).show()
        }

        binding.communityJio2.setOnClickListener {
            Toast.makeText(this, "community jio 2", Toast.LENGTH_SHORT).show()
        }

        binding.communityJio3.setOnClickListener {
            Toast.makeText(this, "community jio 3", Toast.LENGTH_SHORT).show()
        }

        // get info from firebase for residence stub
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val isLogin = sharedPref.getString("email", "1")
        firebaseDb = FirebaseFirestore.getInstance()
        if (isLogin != null) {
            firebaseDb.collection("USERS").document(isLogin).get()
                .addOnSuccessListener {
                    binding.residence.text = it.get("residence").toString()
                }
        }
    }

    //appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}