package com.example.nusupper

import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.nusupper.databinding.ActivityFindJioBinding
import com.example.nusupper.models.Jio
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_find_jio.*

private const val TAG = "FindJioActivity"

class FindJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityFindJioBinding
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var jios: MutableList<Jio>
    private lateinit var adapter: JiosAdapter

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

        // data source always updates
        jios = mutableListOf()

        // create adapter for jios
        adapter = JiosAdapter(this, jios)

        // bind the adapter and layout manager to the recyclerView
        findJiorecyclerviewJios.adapter = adapter

        //onclick stuff
        adapter.setItemClickListener(object: JiosAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(this@FindJio,"you clicked on item $position",Toast.LENGTH_SHORT).show()
            }

        })

        //bind adapter
        findJiorecyclerviewJios.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val snapHelper : SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(findJiorecyclerviewJios)

        // get jio information from firebase
        firebaseDb = FirebaseFirestore.getInstance()
        val jiosReference = firebaseDb.collection("JIOS").limit(20)

        jiosReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "exception when querying posts", exception)
                return@addSnapshotListener
            }
            val jioList = snapshot.toObjects(Jio::class.java)
            jios.clear()
            jios.addAll(jioList)
            adapter.notifyDataSetChanged()
            for (jio in jioList) {
                Log.i(TAG, "Jio $jio")
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