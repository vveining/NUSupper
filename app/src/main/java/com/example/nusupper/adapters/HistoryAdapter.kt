package com.example.nusupper.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.Payment
import com.example.nusupper.R
import com.example.nusupper.ViewFriendsJio
import com.example.nusupper.ViewFriendsProfile
import com.example.nusupper.authentication.Profile
import com.example.nusupper.models.Jio
import com.example.nusupper.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_history.view.*
import kotlinx.android.synthetic.main.item_jio.view.*

class HistoryAdapter(val context: Context, var jios: List<Jio>, val currentUser: User?) :

    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private lateinit var jListener : onItemClickListener


    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: onItemClickListener) {
        jListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view, jListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jios[position])

        //click on username of jio creator
        holder.itemView.user_name.setOnClickListener {
            Toast.makeText(context,"username click", Toast.LENGTH_SHORT).show()
            var clickedJioEmail = jios[position].creatorEmail
            var currentEmail = currentUser?.email
            Toast.makeText(context,"user is $currentEmail, clicked is $clickedJioEmail", Toast.LENGTH_SHORT).show()
            if (clickedJioEmail == currentEmail) {
                var intent = Intent(it.context, Profile::class.java)
                it.context.startActivity(intent)
            } else {
                var intent = Intent(it.context,ViewFriendsProfile::class.java)
                    .putExtra("friend's email",clickedJioEmail)
                    .putExtra("EXTRA_JIOID",jios[position].jioID)
                it.context.startActivity(intent)
            }
        }

        //click on view your order
        holder.itemView.view_your_order.setOnClickListener {
            Toast.makeText(context,"clicked view order",Toast.LENGTH_SHORT).show()
            var newIntent = Intent(context, Payment::class.java)
                .putExtra("EXTRA_JIOID",jios[position].jioID)
            it.context.startActivity(newIntent)
        }
    }

    override fun getItemCount() = jios.size

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(jio: Jio) {

            itemView.rest_name.text = jio.restaurant
            itemView.res_name.text = jio.location
            itemView.ddmmyy.text = jio.closeDate
            itemView.user_name.text = "@" + jio.creator?.username
            itemView.histLogo.setImageResource(Jio.getLogo(jio.restaurant))
        }

        //onclick listener for recycler view
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }

    }
}