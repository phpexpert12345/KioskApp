package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexperts.kioskapp.Models.CartItem
import com.phpexperts.kioskapp.Models.ItemExtraGroup
import com.phpexperts.kioskapp.Models.SubExtraItemsRecord
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.ExtraClick
import kotlinx.android.synthetic.main.layout_extra_item.view.*

class CartAdapter(cartitems:ArrayList<SubExtraItemsRecord>,subExtraItemsRecord: ArrayList<ItemExtraGroup>, context : Context,extraClick: ExtraClick) :RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    var cartitems=cartitems
    var context=context
    var subExtraItemsRecord=subExtraItemsRecord
    var extraClick=extraClick
    var selected_items=ArrayList<SubExtraItemsRecord>()
        fun getSelectedItems():ArrayList<SubExtraItemsRecord>{
            return selected_items
        }

    inner  class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
fun bind(cartitem: SubExtraItemsRecord, context: Context){
itemView.txt_extra_name.text=cartitem.Food_Addons_Name
    itemView.txt_extra_price.text=context.getString(R.string.pound_symbol)+cartitem.Food_Price_Addons
//    Glide.with(context).load(cartitem.cart_item_image).into(itemView.img_extra_item)
    if(cartitem.selected){
        itemView.img_extra_check.setImageResource(R.drawable.ic_checked)
    }
    else {
        itemView.img_extra_check.setImageResource(R.drawable.ic_check_icon)

    }
    itemView.img_less.setOnClickListener{
        var count =itemView.txt_count.text.toString().toInt()
        if(count==0){
            itemView.txt_count.text="0"
        }
        else {
            count-=1
            itemView.txt_count.text=count.toString()
        }
    }
    itemView.img_more.setOnClickListener{
        var count=itemView.txt_count.text.toString().toInt()
       count+=1
        itemView.txt_count.text=count.toString()

    }

    itemView.relative_extra.tag=adapterPosition
    itemView.relative_extra.setOnClickListener{
        val pos=it.tag
        if(selected_items.size>0){
            var is_selected=false
            for(item in selected_items){
               val current_item=cartitems.get(pos as Int)
                if(current_item.equals(item)){
                    is_selected=true
                    break
                }
            }
            if(is_selected){
                selected_items.remove(cartitems.get(pos as Int))
                cartitems.get(pos as Int).selected=false

            }
            else {
                selected_items.add(cartitems.get(pos as Int))
                cartitems.get(pos as Int).selected=true

            }
        }
        else {
            selected_items.add(cartitem)
            cartitems.get(pos as Int).selected=true
        }
        notifyDataSetChanged()
    }

}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_extra_item,null))
    }

    override fun getItemCount(): Int {
        if(subExtraItemsRecord.get(0).Food_addons_selection_Type.equals("Checkbox")){
            return cartitems.size
        }
        else{
            return subExtraItemsRecord.size
        }

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        if(subExtraItemsRecord.get(0).Food_addons_selection_Type.equals("Checkbox")){
            val cartitem=cartitems.get(position)
            holder.itemView.txt_heading.visibility=View.GONE
            holder.itemView.recyler_extra_items.visibility=View.GONE
            holder.itemView.relative_extra.visibility=View.VISIBLE
            holder.bind(cartitem,context)
        }
        else{
            holder.itemView.txt_heading.visibility=View.VISIBLE
            holder.itemView.recyler_extra_items.visibility=View.VISIBLE
            holder.itemView.relative_extra.visibility=View.GONE
            val subExtraItemsRecord=subExtraItemsRecord.get(position)
            holder.itemView.txt_heading.text=subExtraItemsRecord.Food_Group_Name
            val linearlayoutmanager=LinearLayoutManager(context)
            val extraToppingAdapter=ExtraToppingAdapter(subExtraItemsRecord.subExtraItemsRecord,context,extraClick)
            holder.itemView.recyler_extra_items.adapter=extraToppingAdapter
            holder.itemView.recyler_extra_items.layoutManager=linearlayoutmanager


        }

    }
}