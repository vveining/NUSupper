package com.example.nusupper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.adapters.JiosAdapter
import com.example.nusupper.authentication.Profile
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
    private lateinit var yourCurrentJios: MutableList<Jio>
    private lateinit var communityJios: MutableList<Jio>
    private lateinit var allJios: MutableList<Jio>
    private lateinit var yourCurrentJiosAdapter: JiosAdapter
    private lateinit var communityJiosAdapter: JiosAdapter
    private lateinit var allJiosAdapter: JiosAdapter
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
                    val intent = Intent(this,OrderHistory::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // [END navigation drawer things]

        // [START FindJio things start]

        // get signed in user as a User object
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.email?.let {
            firebaseDb.collection("USERS")
                .document(it)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    signedInUser = userSnapshot.toObject(User::class.java)

                    // get residence stub
                    binding.residence.text = signedInUser?.residence.toString()

                    // get jios information from firebase
                    getYourCurrentJiosInformation(yourCurrentJios, yourCurrentJiosAdapter)
                    getCommunityJiosInformation(communityJios, communityJiosAdapter)
                    getAllJiosInformation(allJios, allJiosAdapter)
                }
        }

        // initialise lists and adapter(s)
        initialiseListsAndAdapters()

        // onclick stuff
        enableItemClickListener(yourCurrentJios, yourCurrentJiosAdapter)
        enableItemClickListener(communityJios, communityJiosAdapter)
        enableItemClickListener(allJios, allJiosAdapter)


/////////


        // accept deep link
        val data: Uri? = intent?.data

        if (data != null) {
            val parameters = data.pathSegments
            val intentJioId = parameters[parameters.size - 1]

            firebaseDb.collection("JIOS").document(intentJioId).get()
                .addOnSuccessListener { it ->
                    val creatorEmail = it.get("creatorEmail").toString()
                    if (signedInUser?.email == creatorEmail) { // if user clicks on his own Jio
                        Intent(this, ViewJio::class.java).also {
                            //send JIO ID info to viewJio activity to source for data
                            it.putExtra("EXTRA_JIOID", intentJioId)
                            startActivity(it)
                            finish()
                        }
                    } else { // if user clicks on other's Jio
                        Intent(this, ViewFriendsJio::class.java).also {
                            //send JIO ID info to viewJio activity to source for data
                            it.putExtra("EXTRA_JIOID", intentJioId)
                            startActivity(it)
                            finish()
                        }
                    }

                }

        }


        /////

        // [END FindJio things]
    }

    private fun initialiseListsAndAdapters() {
        // data source always updates
        yourCurrentJios = mutableListOf()
        communityJios = mutableListOf()
        allJios = mutableListOf()

        // create adapter for jios
        yourCurrentJiosAdapter = JiosAdapter(this, yourCurrentJios)
        communityJiosAdapter = JiosAdapter(this, communityJios)
        allJiosAdapter = JiosAdapter(this, allJios)

        // bind the adapter and layout manager to the recyclerView
        yourCurrentJios_recyclerview.adapter = yourCurrentJiosAdapter
        communityJios_recyclerview.adapter = communityJiosAdapter
        allJios_recyclerView.adapter = allJiosAdapter

        // bind adapter
        yourCurrentJios_recyclerview.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        communityJios_recyclerview.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        allJios_recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
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

    private fun enableItemClickListener(jios: MutableList<Jio>, adapter: JiosAdapter) {
        adapter.setItemClickListener(object: JiosAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val jioID = jios[position].jioID
                val creatorEmail = jios[position].creatorEmail
                if (signedInUser?.email == creatorEmail) { // if user clicks on his own Jio
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
    }

    private fun getYourCurrentJiosInformation(yourCurrentJios: MutableList<Jio>, adapter: JiosAdapter) {
        firebaseDb = FirebaseFirestore.getInstance()
        val jiosReference = firebaseDb.collection("JIOS").limit(20)
        jiosReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Toast.makeText(this, "no current jios", Toast.LENGTH_SHORT).show()
            }

            val jioList = snapshot?.toObjects(Jio::class.java)
            yourCurrentJios.clear()
            if (jioList != null) {
                for (i in jioList) {
                    if (i.creatorEmail == signedInUser?.email) {
                        yourCurrentJios.add(i)
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun getCommunityJiosInformation(communityJios: MutableList<Jio>, adapter: JiosAdapter) {
        firebaseDb = FirebaseFirestore.getInstance()
        val jiosReference = firebaseDb.collection("JIOS").limit(20)
        jiosReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Toast.makeText(this, "no current jios", Toast.LENGTH_SHORT).show()
            }

            val jioList = snapshot?.toObjects(Jio::class.java)
            communityJios.clear()
            if (jioList != null) {
                for (i in jioList) {
                    if (i.location == signedInUser?.residence) {
                        communityJios.add(i)
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun getAllJiosInformation(allJios: MutableList<Jio>, adapter: JiosAdapter) {
        firebaseDb = FirebaseFirestore.getInstance()
        val jiosReference = firebaseDb.collection("JIOS").limit(20)
        jiosReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Toast.makeText(this, "no current jios", Toast.LENGTH_SHORT).show()
            }

            val jioList = snapshot?.toObjects(Jio::class.java)
            allJios.clear()
            if (jioList != null) {
                allJios.addAll(jioList)
            }
            adapter.notifyDataSetChanged()
        }
    }
}