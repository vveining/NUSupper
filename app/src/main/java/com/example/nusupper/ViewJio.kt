package com.example.nusupper

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nusupper.databinding.ActivityViewJioBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_view_jio.*

class ViewJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityViewJioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_template)
        binding = ActivityViewJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.viewjio_toolbar)
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        mDrawerLayout = findViewById(R.id.viewjio_drawer_layout)

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
                }
                R.id.createjio -> {
                    Toast.makeText(this, "create a Jio", Toast.LENGTH_SHORT).show()
                }
                R.id.findjio -> {
                    Toast.makeText(this, "find a Jio", Toast.LENGTH_SHORT).show()
                }
                R.id.orderhistory -> {
                    Toast.makeText(this, "order history", Toast.LENGTH_SHORT).show()
                }
            }
            true
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