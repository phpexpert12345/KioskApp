package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.Models.ToppingItems
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.layout_toppings.view.*
import java.text.DecimalFormat

class ToppingAdapter(toppingItems: ArrayList<ToppingItems>, context: Context) :RecyclerView.Adapter<ToppingAdapter.ToppingViewHolder>() {
    var context=context
    var toppingItems=toppingItems
    var decimalFormat= DecimalFormat("##.00")
    inner class ToppingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToppingViewHolder {
        return ToppingViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_toppings, parent, false))
    }

    override fun getItemCount(): Int {
        return  toppingItems.size
    }

    override fun onBindViewHolder(holder: ToppingViewHolder, position: Int) {
        val topping =toppingItems.get(position)
        holder.itemView.txt_toppings.text="+"+topping.topping_name
        val price=topping.topping_price.toString().toDouble()
        holder.itemView.txt_toppings_price.text=context.getString(R.string.pound_symbol)+decimalFormat.format(price)
    }
}