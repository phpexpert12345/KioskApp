package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexperts.kioskapp.Models.ExtraItem
import com.phpexperts.kioskapp.Models.OrderCartItem
import com.phpexperts.kioskapp.Models.ToppingItems
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.CartDatabase
import kotlinx.android.synthetic.main.layout_new_order_item.view.*
import kotlinx.android.synthetic.main.layout_side_menu_item.view.*
import java.text.DecimalFormat

class CancelOrderAdapter(extraitems:ArrayList<OrderCartItem>, context: Context,quantity: Quantity ) : RecyclerView.Adapter<CancelOrderAdapter.CancelOrderViewHolder>() {
    var extraitems=extraitems
    var context=context
    var quantity=quantity

    var decimalFormat= DecimalFormat("##.00")
    interface Quantity{
        fun quantityChanged(price:Double,type:Int)
        fun DeleteClicked(view: View, pos:Int)
    }

   inner class CancelOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
fun bind(extraItem: OrderCartItem, context: Context){
   itemView.txt_extra_menu_item.text=extraItem.item_name
    itemView.txt_order_size.text=extraItem.item_size_type
    itemView.txt_count.text=extraItem.quantity.toString()

    if(extraItem.total_price!=null){
        itemView.order_dish_amount.text= context.getString(R.string.pound_symbol)+decimalFormat.format(extraItem.total_price!!.toDouble()*extraItem.quantity)
    }
    else {
        itemView.order_dish_amount.text= context.getString(R.string.pound_symbol)+decimalFormat.format(extraItem.item_price!!.toDouble()*extraItem.quantity)
    }

    Glide.with(context).load(extraItem.item_image).into(itemView.img_order_dish)
    itemView.img_less.setOnClickListener{
        var count=itemView.txt_count.text.toString().toInt()
        if(count==1){
            itemView.txt_count.text="1"
            var amount_price=extraItem.item_price!!.toDouble()
            itemView.order_dish_amount.text=context.getString(R.string.pound_symbol)+decimalFormat.format(amount_price)
        }
        else {
            count -= 1
            var price =0.0
            if(extraItem.total_price!=null ){
                price =extraItem.total_price.toString().toDouble()
            }
            else {
               price=extraItem.item_price!!.toDouble()

            }
//            var price =extraItem.item_price!!.toDouble()
            itemView.txt_count.text=count.toString()
            var amount=count*price
            quantity.quantityChanged(price,0)
            itemView.order_dish_amount.text=context.getString(R.string.pound_symbol)+decimalFormat.format(amount)
        }
    }
    itemView.img_more.setOnClickListener{
        var count=itemView.txt_count.text.toString().toInt()
        count += 1
        var price =0.0
        if(extraItem.total_price!=null ){
            price =extraItem.total_price.toString().toDouble()
        }
        else {
            price=extraItem.item_price!!.toDouble()

        }
        itemView.txt_count.text=count.toString()
        var amount=count*price
        quantity.quantityChanged(price,1)
        itemView.order_dish_amount.text=context.getString(R.string.pound_symbol)+decimalFormat.format(amount)
    }
    itemView.img_delete.tag=adapterPosition
    itemView.img_delete.setOnClickListener {
        val pos=it.tag
        quantity.DeleteClicked(it, pos as Int)
    }
    getToppingsfromDataBase(this,extraItem.item_name!!)

}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelOrderViewHolder {
       return CancelOrderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_new_order_item,parent,false))
    }

    override fun getItemCount(): Int {
       return extraitems.size
    }

    override fun onBindViewHolder(holder: CancelOrderViewHolder, position: Int) {
val extraItem=extraitems.get(position)
        holder.bind(extraItem,context)
    }
    fun getToppingsfromDataBase(holder: CancelOrderViewHolder,item_name:String){
        val cartDatabase=CartDatabase.getDataBase(context)
        val toppingDao=cartDatabase!!.ToppingDao()
        var toppings_array=toppingDao!!.getToppingsbyItem(item_name)
        if(toppings_array.size>0){
            val toppingAdapter=ToppingAdapter(toppings_array as ArrayList<ToppingItems>,context)
            val linearLayoutManager=LinearLayoutManager(context)
            holder.itemView.recycler_toppings.adapter=toppingAdapter
            holder.itemView.recycler_toppings.layoutManager=linearLayoutManager
        }

    }

}