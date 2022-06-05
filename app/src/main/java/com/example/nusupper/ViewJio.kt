package com.example.nusupper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class ViewJio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_jio)

        val bundle:Bundle? = intent.extras
        val newJio:JioDetails = bundle!!.getSerializable("new Jio") as JioDetails
        // !! means it is not null, since the ? in bundle makes the call safe

        val jioTitle = newJio.title

        Toast.makeText(this, jioTitle,Toast.LENGTH_SHORT).show()
    }
}