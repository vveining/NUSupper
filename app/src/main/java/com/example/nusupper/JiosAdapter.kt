package com.example.nusupper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.models.Jio
import kotlinx.android.synthetic.main.item_jio.view.*

class JiosAdapter (val context: Context, val jios: List<Jio>) :

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

            itemView.textView.text = jio.restaurant
            itemView.textView2.text = jio.location
            itemView.textView3.text = jio.closeTime.toString()
            itemView.textView4.text = jio.creator?.username
            //add image for restaurant here
        }

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    //onclick listener for recycler view

}