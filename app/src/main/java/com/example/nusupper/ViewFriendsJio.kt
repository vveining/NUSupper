package com.example.nusupper

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nusupper.databinding.ActivityViewFriendsJioBinding
import com.example.nusupper.models.Jio
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ViewFriendsJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityViewFriendsJioBinding
    private lateinit var jioID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFriendsJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [START navigation drawer things]

        jioID = intent.getStringExtra("EXTRA_JIOID").toString()

        val toolbar: Toolbar = findViewById(R.id.viewfriendsjio_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.viewfriendsjio_drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.viewfriendsjio_nav_view)
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

        // [END navigation drawer things]

        // [START ViewJio things]

        // button tooltips
        TooltipCompat.setTooltipText(binding.copyjiolinkButton, "copy link")

        setViewJio(jioID)

        // [END ViewJio things]
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

    private fun setViewJio(jioID: String?) {
        val firebaseDb = FirebaseFirestore.getInstance()
        if (jioID != null) {
            firebaseDb.collection("JIOS").document(jioID).get()
                .addOnSuccessListener {
                    binding.viewJioDelStub.text = it.get("location").toString()
                    val restaurant: String = it.get("restaurant").toString()
                    binding.viewJioRestaurantStub.text = restaurant
                    val closeTime = it.get("close time").toString()
                    val closeDate = it.get("close date").toString()
                    binding.viewJioDetailsStub.text = "$closeTime, $closeDate"
                    binding.restaurantImage.setImageResource(Jio.getLogo(restaurant))
                    val userData = it.get("creator") as Map<*, *>
                    binding.jioOwnerStub.text = userData["username"].toString()
                }
        }
    }
}