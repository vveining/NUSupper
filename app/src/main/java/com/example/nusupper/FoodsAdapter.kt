package com.example.nusupper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.nusupper.models.Food

class FoodsAdapter(val context: Context, var foodList: MutableList<Food>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return foodList.size
    }

    override fun getItem(p0: Int): Any {
        return foodList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val thisFood = foodList[p0]

        val view: View
        val viewHolder : ListViewHolder

        if (p1 == null) {
            view = inflater.inflate(R.layout.item_food, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ListViewHolder
        }

        viewHolder.nameLabel.text = thisFood.foodName
        viewHolder.priceLabel.text = thisFood.price.toString()
        viewHolder.quantityLabel.text = thisFood.qty.toString()
        viewHolder.remarksLabel.text = thisFood.remarks

        return view
    }

    private class ListViewHolder(row: View?) {
        val nameLabel = row!!.findViewById(R.id.food_stub) as TextView
        val priceLabel = row!!.findViewById(R.id.price_stub) as TextView
        val quantityLabel = row!!.findViewById(R.id.number_stub) as TextView
        val remarksLabel = row!!.findViewById(R.id.remarks_stub) as TextView
    }

}