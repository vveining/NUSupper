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
import com.example.nusupper.databinding.ActivityViewJioBinding
import com.example.nusupper.models.Jio
import com.example.nusupper.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.add_order_alertdialog.*
import kotlinx.android.synthetic.main.close_order_dialog.*
import kotlinx.android.synthetic.main.settings_alertdialog.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class ViewJio : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityViewJioBinding
    private lateinit var jioID: String
    private var firebaseInst = FirebaseFirestore.getInstance()
    private lateinit var jioLink: String
    private lateinit var jio: Jio
    private var signedInUser: User? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var dtNow: LocalDateTime = LocalDateTime.now()
    private val df = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val tf = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_template)
        binding = ActivityViewJioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //INTENT
        jioID = intent.getStringExtra("EXTRA_JIOID").toString()

        // get signed in user as a User object -> i moved this chunk up because laggy
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.email?.let {
            firebaseInst.collection("USERS")
                .document(it)
                .get()
                .addOnSuccessListener { userSnapshot ->
                    signedInUser = userSnapshot.toObject(User::class.java)
                    //get current jio
                    getJio(jioID)

                }
        }

        // [START navigation drawer things]
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

        // button tooltips
        TooltipCompat.setTooltipText(binding.editjioButton, "edit Jio")
        TooltipCompat.setTooltipText(binding.copyjiolinkButton, "copy link")

        //date and time toast
        //Toast.makeText(this,dtNow.toString(),Toast.LENGTH_SHORT).show()

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

        // edit jio
        binding.editjioButton.setOnClickListener {
            Intent(this,EditJio::class.java).also {
                //send JIO ID info to viewJio activity to source for data
                it.putExtra("EXTRA_JIOID",jioID)
                startActivity(it)
            }
        }

        //onclick for close order
        binding.closeorderButton.setOnClickListener{
            if (jio.open) {
                // ask if user wants to close order
                // build and show alertdialog popup
                val closeDialogView = LayoutInflater.from(this).inflate(R.layout.close_order_dialog, null) //CHANGE R.id
                val closeDialogBuilder = AlertDialog.Builder(this)
                    .setView(closeDialogView)
                    .setTitle("Close your Jio?")
                val alertDialog = closeDialogBuilder.show()
                alertDialog.closeJio.setOnClickListener{
                    Toast.makeText(this@ViewJio,"close Jio clicked", Toast.LENGTH_SHORT).show()
                    firebaseInst.collection("JIOS").document(jioID)
                        .update("open", false) // update firebase database
                    Intent(this@ViewJio,ViewJio::class.java).also {
                        it.putExtra("EXTRA_JIOID",jioID)
                        startActivity(it)
                    }
                }
                alertDialog.closeBack.setOnClickListener {
                    alertDialog.dismiss()
                }
            } else { // close order button is hidden if order is closed, this is a safety gate
                // alert that order is already closed
                Toast.makeText(this@ViewJio,"Jio is already closed!", Toast.LENGTH_SHORT).show()
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

    private fun setViewJio() {
        binding.viewJioDelStub.text = jio.location
        val restaurant: String = jio.restaurant
        binding.viewJioRestaurantStub.text = restaurant
        val closeTime = jio.closeTime
        val closeDate = jio.closeDate
        binding.viewJioDetailsStub.text = "$closeTime, $closeDate"
        binding.restaurantImage.setImageResource(Jio.getLogo(restaurant))
        val jioId = jio.jioID
        jioLink = "http://www.nusupper.com/viewjio/$jioId"
        binding.linkStub.text = jioLink
    }

    private fun getJio(jioID: String?) { //handle jio info & open/close
        if (jioID != null) {
            firebaseInst.collection("JIOS").document(jioID).get()
                .addOnSuccessListener {
                    jio = it.toObject<Jio>()!!
                    setViewJio() // display texts in layout

                    // format localdate & localtime
                    var dateLD = LocalDate.parse(jio.closeDate,df)
                    var timeLT = LocalTime.parse(jio.closeTime,tf)
                    var closeLDT = LocalDateTime.of(dateLD,timeLT) //format localDT
                    var shouldClose = dtNow.isAfter(closeLDT) // if current time is AFTER close time

                    if (shouldClose && jio.open) { // currently open but needs to close
                        jio = jio.closeJio() //update local jio
                        firebaseInst.collection("JIOS").document(jioID)
                            .update("open", false) // update firebase database
                    }

                    if (!jio.open) { //if jio closed, make alert visible and Current Orders gone
                        binding.closeorderButton.visibility = View.GONE
                        binding.closedAlert.visibility = View.VISIBLE
                    }

                    setButtons() // set onclick functions for button(s)
                }
        }
    }
}