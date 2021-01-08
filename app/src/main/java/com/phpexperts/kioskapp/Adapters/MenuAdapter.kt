package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexperts.kioskapp.Models.MenuItem
import com.phpexperts.kioskapp.Models.SubItemRecords
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.layout_menu_item.view.*

class MenuAdapter(menuitems:ArrayList<SubItemRecords>, context:Context, addClciked: AddClciked) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    var menuitems=menuitems
    var context=context
    interface AddClciked{
        fun Clicked(view: View, position: Int)
    }
    var addClciked=addClciked
    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
fun bind(menuItem: SubItemRecords,context: Context,addClciked: AddClciked){
    itemView.txt_menu_title.text=menuItem.RestaurantPizzaItemName
    itemView.txt_menu_price.text=context.getString(R.string.pound_symbol)+menuItem.RestaurantPizzaItemPrice
    itemView.txt_discounted_price.text=context.getString(R.string.pound_symbol)+menuItem.RestaurantPizzaItemOldPrice
    itemView.txt_discounted_price.setPaintFlags(itemView.txt_discounted_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
    Glide.with(context).load(menuItem.food_Icon).placeholder(R.drawable.ic_palceholder).into(itemView.img_menu_item)
    itemView.txt_add.setTag(adapterPosition)
    itemView.txt_add.setOnClickListener{  
        val pos=it.tag
        addClciked.Clicked(it, pos as Int)
    }

}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_menu_item,null))
    }

    override fun getItemCount(): Int {
      return menuitems.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
val menuItem=menuitems.get(position)
        holder.bind(menuItem,context,addClciked)
    }
}