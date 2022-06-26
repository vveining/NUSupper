package com.example.nusupper

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.nusupper.helpers.ModifyFood
import com.example.nusupper.models.Food
import java.math.RoundingMode
import java.text.DecimalFormat

class FoodsAdapter(val context: Context, var foodList: MutableList<Food>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)
    private var modifyFood: ModifyFood = context as ModifyFood
    private val df = DecimalFormat("#.##")

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
        var thisFood = foodList[p0]

        val view: View
        val viewHolder: ListViewHolder

        if (p1 == null) {
            view = inflater.inflate(R.layout.item_food, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ListViewHolder
        }

        df.roundingMode = RoundingMode.UP

        viewHolder.nameLabel.text = thisFood.foodName
        viewHolder.totalPriceLabel.text = df.format(thisFood.totalPrice).toString()
        viewHolder.quantityLabel.text = thisFood.qty.toString()
        viewHolder.remarksLabel.text = thisFood.remarks

        viewHolder.addQtyButton.setOnClickListener {
            thisFood = modifyFood.addFoodQtyToMap(thisFood.foodName)!!
            viewHolder.totalPriceLabel.text = df.format(thisFood.totalPrice).toString()
            viewHolder.quantityLabel.text = thisFood.qty.toString()
            modifyFood.refreshPage()
        }

        viewHolder.removeQtyButton.setOnClickListener {
            if (modifyFood.checkIfFoodPresent(thisFood.foodName)) {
                thisFood = modifyFood.removeFoodQtyToMap(thisFood.foodName)!!
                viewHolder.totalPriceLabel.text = df.format(thisFood.totalPrice).toString()
                viewHolder.quantityLabel.text = thisFood.qty.toString()
                modifyFood.refreshPage()
            }
        }

        return view
    }

    private class ListViewHolder(row: View?) {
        val nameLabel = row!!.findViewById(R.id.food_stub) as TextView
        val totalPriceLabel = row!!.findViewById(R.id.price_stub) as TextView
        val quantityLabel = row!!.findViewById(R.id.number_stub) as TextView
        val remarksLabel = row!!.findViewById(R.id.remarks_stub) as TextView
        val addQtyButton = row!!.findViewById(R.id.add_an_order_button) as ImageButton
        val removeQtyButton = row!!.findViewById(R.id.remove_an_order_button) as ImageButton
    }

}