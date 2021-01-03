package com.phpexperts.kioskapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.R

class OrderConfirmedAdapter : RecyclerView.Adapter<OrderConfirmedAdapter.OrderConfirmedViewHolder>()
{
    class OrderConfirmedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderConfirmedViewHolder {
        return  OrderConfirmedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_order_item,null))
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: OrderConfirmedViewHolder, position: Int) {

    }
}