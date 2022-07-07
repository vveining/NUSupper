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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.adapters.HistoryAdapter
import com.example.nusupper.authentication.Profile
import com.example.nusupper.databinding.ActivityOrderHistoryBinding
import com.example.nusupper.models.Jio
import com.example.nusupper.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_order_history.*

class OrderHistory : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var hist: MutableList<Jio>
    private lateinit var histAdapter: HistoryAdapter
    private var signedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.view_orderhist_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.view_orderhist_drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.orderhist_nav_view)
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

        // get signed in user as a User object
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.email?.let {
            firebaseDb.collection("USERS")
                .document(it)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    signedInUser = userSnapshot.toObject(User::class.java)

                    // initialise lists and adapter(s)
                    initialiseListsAndAdapters()

                    // get jios information from firebase
                    getJioOrderHist(hist, histAdapter)

                    //onclick stuff
                    enableItemClickListener(hist,histAdapter)

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

    //get all Jios that you have orders in
    private fun getJioOrderHist(hist: MutableList<Jio>, adapter: HistoryAdapter) {
        firebaseDb = FirebaseFirestore.getInstance()
        val jiosReference = firebaseDb.collection("JIOS").limit(20)
        jiosReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Toast.makeText(this, "no past jio orders", Toast.LENGTH_SHORT).show()
            }

            val jioList = snapshot?.toObjects(Jio::class.java)
            hist.clear()
            if (jioList != null) {
                for (i in jioList) { //check each jio
                    var foodList = i.foodArr
                    if (foodList != null) {
                        for (j in foodList) { //check each food item in the jio until there is at least one item belonging to user
                            if (j.username == signedInUser?.username) {
                                hist.add(i)
                                break
                            }
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun initialiseListsAndAdapters() {
        // data source always updates
        hist = mutableListOf()

        // create adapter for jios
        histAdapter = HistoryAdapter(this, hist,signedInUser)

        // bind the adapter and layout manager to the recyclerView
        viewHist_recycler.adapter = histAdapter

        // bind adapter
        viewHist_recycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun enableItemClickListener(jios: MutableList<Jio>, adapter: HistoryAdapter) {
        adapter.setItemClickListener(object: HistoryAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(this@OrderHistory,"you clicked order $position",Toast.LENGTH_SHORT).show()
                var newIntent = Intent(this@OrderHistory,Payment::class.java)
                startActivity(newIntent.putExtra("jioID",jios[position].jioID))
            }
        })
    }
}