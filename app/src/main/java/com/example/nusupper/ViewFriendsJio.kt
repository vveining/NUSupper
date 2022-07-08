package com.example.nusupper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nusupper.authentication.Profile
import com.example.nusupper.databinding.ActivityViewFriendsJioBinding
import com.example.nusupper.models.Jio
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.settings_alertdialog.*

class ViewFriendsJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityViewFriendsJioBinding
    private lateinit var jioID: String
    private var email = "0"
    private lateinit var jioLink: String

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
                    val intent = Intent(this,OrderHistory::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // [END navigation drawer things]

        // [START ViewJio things]

        setViewJio(jioID)

        // button tooltips
        TooltipCompat.setTooltipText(binding.copyjiolinkButton, "copy link")

        // set onclick functions for button(s)
        setButtons()

        //retrive Jio Owner's email
        val db = FirebaseFirestore.getInstance()
        db.collection("JIOS").document(jioID).get()
            .addOnSuccessListener {
                val getJio = it.toObject<Jio>()       //convert jio to object
                if (getJio != null) {                // once done, treat jio object normally, use kotlin functions
                    email = getJio.creator?.email.toString()

                    if (!getJio.open) {
                        binding.viewFriendsJioClosedAlert.visibility = View.VISIBLE
                    }
                }
            }

        // [END ViewJio things]

    }

    private fun setButtons() {
        // onclick for view current orders
        binding.viewcurrentordersButton.setOnClickListener {
            Intent(this,JioOrders::class.java).also {
                //send JIO ID info to viewJio activity to source for data
                it.putExtra("EXTRA_JIOID",jioID)
                startActivity(it)
            }
        }

        // onclick for view payment
        binding.paymentButton.setOnClickListener {
            Intent(this,Payment::class.java).also {
                //send JIO ID info to viewJio activity to source for data
                it.putExtra("EXTRA_JIOID",jioID)
                startActivity(it)
            }
        }

        //click on username
        binding.jioOwnerStub.setOnClickListener {
            Intent(this,ViewFriendsProfile::class.java).also {
                it.putExtra("friend's email",email)
                it.putExtra("EXTRA_JIOID", jioID)
                startActivity(it)
            }
        }

        // copy jio link to clipboard
        binding.copyjiolinkButton.setOnClickListener {
            val clipboard : ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip : ClipData = ClipData.newPlainText("copy link", jioLink)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "copied Jio link", Toast.LENGTH_SHORT).show()

            // build and show alertdialog popup
            val dialogView = LayoutInflater.from(this).inflate(R.layout.settings_alertdialog, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("to open Jio links")
            val alertDialog = dialogBuilder.show()

            // dismiss alertDialog
            alertDialog.dismissButton.setOnClickListener {
                alertDialog.dismiss()
            }

            // redirect user to app settings
            alertDialog.gotoSettingsButton.setOnClickListener {
                val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + "com.example.nusupper"))
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(myAppSettings)
                alertDialog.dismiss()
            }

            alertDialog.show()
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
                    binding.jioOwnerStub.text = it.get("creatorUsername").toString()
                    val jioId = it.get("jioID").toString()
                    jioLink = "http://www.nusupper.com/viewjio/$jioId"
                    binding.linkStub.text = jioLink
                }
        }
    }


}