package com.example.nusupper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.models.Jio
import kotlinx.android.synthetic.main.item_jio.view.*

class JiosAdapter (val context: Context, var jios: List<Jio>) :

    RecyclerView.Adapter<JiosAdapter.ViewHolder>() {

    private lateinit var jListener : OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)

    }

    fun setItemClickListener(listener: OnItemClickListener) {
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

    inner class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(jio: Jio) {

            itemView.textView.text = jio.restaurant
            itemView.textView2.text = jio.location
            itemView.textView3.text = jio.closeDT
            itemView.textView4.text = jio.creator?.username
            itemView.restaurant_logo_stub.setImageResource(getLogo(jio.restaurant))

            //add image for restaurant here
        }

        //onclick listener for recycler view
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    fun getLogo(name: String) : Int {
        when (name) {
            "Super Snacks" -> return R.drawable.supersnacks
            "Starbucks" -> return R.drawable.starbucks
            "Fong Seng" -> return R.drawable.fongseng
            "McDonalds" -> return R.drawable.macs
            "Al-Amaan" -> return R.drawable.al_amaan
        }
        return R.drawable.food_icon
    }


}