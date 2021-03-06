package com.example.nusupper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.nusupper.R
import com.example.nusupper.helpers.ModifyFood
import com.example.nusupper.helpers.PaymentHelpers
import com.example.nusupper.models.Food
import java.math.RoundingMode
import java.text.DecimalFormat

class PaymentBillAdapter(val context: Context,
                         private var usernameList: MutableList<String>,
                         var userFoodList: HashMap<String, MutableList<Food>>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)
    private var paymentHelpers: PaymentHelpers = context as PaymentHelpers
    private val df = DecimalFormat("#.##")

    override fun getCount(): Int {
        return usernameList.size
    }

    override fun getItem(p0: Int): Any {
        return usernameList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val thisUsername = usernameList[p0]

        val view: View
        val viewHolder: ListViewHolder

        if (p1 == null) {
            view = inflater.inflate(R.layout.item_payment_bill, p2, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        } else {
            view = p1
            viewHolder = view.tag as ListViewHolder
        }

        df.roundingMode = RoundingMode.UP

        viewHolder.nameLabel.text = thisUsername
        viewHolder.totalPriceLabel.text = df.format(
            paymentHelpers.getUsersTotalBill(thisUsername, userFoodList)).toString()

        return view
    }

    private class ListViewHolder(row: View?) {
        val nameLabel = row!!.findViewById(R.id.name_stub) as TextView
        val totalPriceLabel = row!!.findViewById(R.id.price_stub) as TextView
    }
}