package com.example.nusupper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nusupper.R
import com.example.nusupper.models.Jio
import kotlinx.android.synthetic.main.item_history.view.*
import kotlinx.android.synthetic.main.item_jio.view.*

class HistoryAdapter (val context: Context, var jios: List<Jio>) :

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