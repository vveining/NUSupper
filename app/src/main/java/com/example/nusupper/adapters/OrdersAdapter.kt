package com.example.nusupper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.nusupper.R
import com.example.nusupper.helpers.ModifyFood
import com.example.nusupper.models.Food
import java.math.RoundingMode
import java.text.DecimalFormat

class OrdersAdapter(val context: Context,
                    private val usernameList: List<String>,
                    private val userFoodList: HashMap<String, MutableList<Food>>): BaseExpandableListAdapter() {

    private val inflater = LayoutInflater.from(context)
    private val df = DecimalFormat("#.##")
    private var modifyFood: ModifyFood = context as ModifyFood

    override fun getGroupCount(): Int {
        return usernameList.size
    }

    override fun getChildrenCount(p0: Int): Int {
        return userFoodList[usernameList[p0]]!!.size
    }

    override fun getGroup(p0: Int): Any {
        return usernameList[p0]
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return userFoodList[usernameList[p0]]!![p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val view: View
        val listTitle = getGroup(p0) as String
        if (p2 == null) {
            view = inflater.inflate(R.layout.item_orders_username, p3, false)
        } else {
            view = p2
        }
        val listTitleTextView = view.findViewById<TextView>(R.id.usernameStub)
        listTitleTextView.text = listTitle

        return view
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val view: View
        val viewHolder: ListViewHolder

        var thisFood = getChild(p0, p1) as Food

        if (p3 == null) {
            view = inflater.inflate(R.layout.item_food, p4, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p3
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

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
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