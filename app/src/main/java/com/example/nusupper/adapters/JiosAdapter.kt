package com.example.nusupper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.R
import com.example.nusupper.models.Jio
import kotlinx.android.synthetic.main.item_jio.view.*

class JiosAdapter (val context: Context, var jios: List<Jio>) :

    RecyclerView.Adapter<JiosAdapter.ViewHolder>() {

    private lateinit var jListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setItemClickListener(listener: onItemClickListener) {
        jListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_jio, parent, false)
        return ViewHolder(view, jListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jios[position])
    }

    override fun getItemCount() = jios.size

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(jio: Jio) {

            itemView.textView_restaurant.text = jio.restaurant
            itemView.textView_location.text = jio.location
            itemView.textView_creator.text = jio.creator?.username
            itemView.restaurant_logo_stub.setImageResource(Jio.getLogo(jio.restaurant))
            if (!jio.open) {
                itemView.textView_closeDate.visibility = View.GONE
                itemView.textView_closeTime.text = "JIO CLOSED"
            } else {
                itemView.textView_closeDate.text = jio.closeDate
                itemView.textView_closeTime.text = "closes " + jio.closeTime
            }

        }

        //onclick listener for recycler view
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}