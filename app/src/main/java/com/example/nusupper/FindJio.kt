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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.databinding.ActivityFindJioBinding
import com.example.nusupper.models.Jio
import com.example.nusupper.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_find_jio.*

class FindJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityFindJioBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var jios: MutableList<Jio>
    private lateinit var adapter: JiosAdapter
    private var signedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityFindJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [START navigation drawer things]

        val toolbar: Toolbar = findViewById(R.id.findjio_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.findjio_drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.viewjio_nav_view)
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

        // [START FindJio things start]

        // clicking on individual jio buttons
        binding.communityJio1.setOnClickListener {
            Toast.makeText(this, "community jio 1", Toast.LENGTH_SHORT).show()
        }
        binding.communityJio2.setOnClickListener {
            Toast.makeText(this, "community jio 2", Toast.LENGTH_SHORT).show()
        }
        binding.communityJio3.setOnClickListener {
            Toast.makeText(this, "community jio 3", Toast.LENGTH_SHORT).show()
        }

        // get signed in user as a User object
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.email?.let {
            firebaseDb.collection("USERS")
                .document(it)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    signedInUser = userSnapshot.toObject(User::class.java)
                }
        }

        // get residence stub
        binding.residence.text = signedInUser?.residence.toString()

        // data source always updates
        jios = mutableListOf()

        // create adapter for jios
        adapter = JiosAdapter(this, jios)

        // bind the adapter and layout manager to the recyclerView
        findJiorecyclerviewJios.adapter = adapter

        // onclick stuff
        adapter.setItemClickListener(object: JiosAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val jioID = jios[position].jioID
                val creatorName = jios[position].creatorName
                Toast.makeText(this@FindJio,"you clicked on item $jioID",Toast.LENGTH_SHORT).show()
                if (signedInUser?.name == creatorName) { // if user clicks on his own Jio
                    Intent(this@FindJio, ViewJio::class.java).also {
                        //send JIO ID info to viewJio activity to source for data
                        it.putExtra("EXTRA_JIOID", jioID)
                        startActivity(it)
                        finish()
                    }
                } else { // if user clicks on other's Jio
                    Intent(this@FindJio, ViewFriendsJio::class.java).also {
                        //send JIO ID info to viewJio activity to source for data
                        it.putExtra("EXTRA_JIOID", jioID)
                        startActivity(it)
                        finish()
                    }
                }
            }
        })

        // bind adapter
        findJiorecyclerviewJios.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // get jio information from firebase
        firebaseDb = FirebaseFirestore.getInstance()
        val jiosReference = firebaseDb.collection("JIOS").limit(20)
        jiosReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Toast.makeText(this, "no current jios", Toast.LENGTH_SHORT).show()
            }
            val jioList = snapshot?.toObjects(Jio::class.java)
            jios.clear()
            if (jioList != null) {
                jios.addAll(jioList)
            }
            adapter.notifyDataSetChanged()
        }

        // [END FindJio things]
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
}