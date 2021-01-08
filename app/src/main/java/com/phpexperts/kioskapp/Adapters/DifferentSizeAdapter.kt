package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.Models.RestaurantMenItemsSize
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.differentsize_item.view.*
import kotlinx.android.synthetic.main.layout_side_menu_item.view.*

class DifferentSizeAdapter(sizeitems:ArrayList<RestaurantMenItemsSize>, sizeClicked: SizeClicked, context: Context) :RecyclerView.Adapter<DifferentSizeAdapter.DifferentSizeViewHolder>() {
    var sizeitems=sizeitems
    var selected_position =0
    var context=context
    interface SizeClicked{
        fun Clicked(view: View, pos:Int)
    }
    var sizeClicked=sizeClicked
    inner class DifferentSizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
fun bind(restaurantMenItemsSize: RestaurantMenItemsSize){
    if(restaurantMenItemsSize.RestaurantPizzaItemName!=null) {
        val name = restaurantMenItemsSize.RestaurantPizzaItemName
        itemView.txt_sizes.text = name
    }

}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DifferentSizeViewHolder {
      return  DifferentSizeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.differentsize_item,parent,false))
    }

    override fun getItemCount(): Int {
return  sizeitems.size
    }

    override fun onBindViewHolder(holder: DifferentSizeViewHolder, position: Int) {
holder.bind(sizeitems.get(position))
        holder.itemView.txt_sizes.tag=position
        holder.itemView.txt_sizes.setOnClickListener {
            val pos=it.tag
            selected_position= pos as Int
            sizeClicked.Clicked(it, pos as Int)
            notifyDataSetChanged()

        }
        if (selected_position==position) {
            holder.itemView.txt_sizes.background=context!!.getDrawable(R.drawable.background_filled_green)
            holder.itemView.txt_sizes.setTextColor(context!!.getColor(R.color.white))

        } else {
            holder.itemView.txt_sizes.background=context!!.getDrawable(R.drawable.background_grey_stroke)
            holder.itemView.txt_sizes.setTextColor(context!!.getColor(R.color.cart_empty))

        }
    }
}