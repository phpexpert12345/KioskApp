package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexperts.kioskapp.Models.OrderCartItem
import com.phpexperts.kioskapp.Models.SingleCom
import com.phpexperts.kioskapp.Models.ToppingItems
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.CartDatabase
import kotlinx.android.synthetic.main.extra_item_radio.view.*
import kotlinx.android.synthetic.main.layout_com_cart.view.*
import kotlinx.android.synthetic.main.layout_new_order_item.view.*
import kotlinx.android.synthetic.main.layout_new_order_item.view.img_delete
import kotlinx.android.synthetic.main.layout_new_order_item.view.img_less
import kotlinx.android.synthetic.main.layout_new_order_item.view.img_more
import kotlinx.android.synthetic.main.layout_new_order_item.view.img_order_dish
import kotlinx.android.synthetic.main.layout_new_order_item.view.order_dish_amount
import kotlinx.android.synthetic.main.layout_new_order_item.view.txt_count
import kotlinx.android.synthetic.main.layout_new_order_item.view.txt_extra_menu_item
import kotlinx.android.synthetic.main.layout_new_order_item.view.txt_order_size
import java.text.DecimalFormat

class CancelOrderAdapter(extraitems:ArrayList<OrderCartItem>, context: Context,quantity: Quantity ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var extraitems=extraitems
    var context=context
    var quantity=quantity
    private val COMbO = 100
    private val SIZE = 200

    var decimalFormat= DecimalFormat("##.00")
    interface Quantity{
        fun quantityChanged(price:Double,type:Int, position: Int)
        fun DeleteClicked(view: View, pos:Int)
        fun comquantityChanged(price:Double,type:Int,pos: Int)
        fun comDeleteClicked(view: View, pos:Int,extraitems:ArrayList<OrderCartItem>)
    }

    override fun getItemViewType(position: Int): Int {
        if(!extraitems.get(position).com){
            return SIZE
        }
        else{
            return COMbO
        }
    }
    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

   inner class CancelOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
fun bind(extraItem: OrderCartItem, context: Context,position: Int){
   itemView.txt_extra_menu_item.text=extraItem.item_name
    itemView.txt_order_size.text=extraItem.item_size_type
    itemView.txt_count.text=extraItem.quantity.toString()

    if(extraItem.total_price!=null){
        itemView.order_dish_amount.text= context.getString(R.string.pound_symbol)+decimalFormat.format(extraItem.total_price!!.toDouble()*extraItem.quantity)
    }
    else {
        itemView.order_dish_amount.text= context.getString(R.string.pound_symbol)+decimalFormat.format(extraItem.item_price!!.toDouble()*extraItem.quantity)
    }

    Glide.with(context).load(extraItem.item_image).placeholder(R.drawable.ic_palceholder).into(itemView.img_order_dish)
    itemView.img_less.tag=position
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
            val pos=it.tag
            itemView.txt_count.text=count.toString()
            var amount=count*price
            quantity.quantityChanged(price,0, pos as Int)
            itemView.order_dish_amount.text=context.getString(R.string.pound_symbol)+decimalFormat.format(amount)
        }
    }
    itemView.img_more.tag=position
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
        val pos=it.tag
        itemView.txt_count.text=count.toString()
        var amount=count*price
        quantity.quantityChanged(price,1, pos as Int)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==SIZE){
            return CancelOrderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_new_order_item,parent,false))
        }
        else{
            return SingleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_com_cart,parent,false))
        }

    }

    override fun getItemCount(): Int {
       return extraitems.size
    }

    override fun onBindViewHolder(hold: RecyclerView.ViewHolder, position: Int) {
val extraItem=extraitems.get(position)
        if(!extraItem.com) {
            val holder:CancelOrderViewHolder= hold as CancelOrderViewHolder
            holder.bind(extraItem, context, position)
        }
        else{
            val holder:SingleViewHolder= hold as SingleViewHolder
            holder.itemView.txt_extra_menu_item.text=extraItem.item_name
            holder.itemView.txt_count.text=extraItem.quantity.toString()
            holder.itemView.order_dish_amount.text=context.getString(R.string.pound_symbol)+extraItem.total_price!!.toDouble()*extraItem.quantity
            val singleComs: MutableList<SingleCom> = java.util.ArrayList<SingleCom>()
            if (extraItem.item_image!!.contains(",")) {
                val sec: Array<String> = extraItem.item_image!!.split(",").toTypedArray()
                val size: Array<String> = extraItem.item_size_type!!.split(",").toTypedArray()
                val tops: Array<String> = extraItem.item_size_id!!.split(",").toTypedArray()
                for (i in sec.indices) {
                    val singleCom = SingleCom()
                    singleCom.section = sec[i]
                    singleCom.size = size[i]
                    singleCom.tops = tops[i]
                    singleComs.add(singleCom)
                }
            } else {
                val singleCom = SingleCom()
                singleCom.section = extraItem.item_image
                singleCom.size = extraItem.item_size_type
                singleCom.tops = extraItem.item_size_id
                singleComs.add(singleCom)
            }
            if (singleComs.size > 0) {
                val linearLayoutManager = LinearLayoutManager(context)
                val singleComAdapter = SingleComAdapter(singleComs)
                holder.itemView.recycler_com_item.setAdapter(singleComAdapter)
                holder.itemView.recycler_com_item.setLayoutManager(linearLayoutManager)
            }
            holder.itemView.img_less.tag=position
            holder.itemView.img_less.setOnClickListener {
                var count = holder.itemView.txt_count.text.toString().toInt()
                val pos = it.tag
                if (count == 1) {
                    quantity.comDeleteClicked(it, pos as Int,extraitems)
                } else {
                    count -= 1
                    var price = 0.0
                    if (extraItem.total_price != null) {
                        price = extraItem.total_price.toString().toDouble()
                    } else {
                        price = extraItem.item_price!!.toDouble()

                    }
//            var price =extraItem.item_price!!.toDouble()

                    holder.itemView.txt_count.text = count.toString()
                    var amount = count * price
                    quantity.comquantityChanged(price, 0, pos as Int)
                    holder.itemView.order_dish_amount.text =
                        context.getString(R.string.pound_symbol) + decimalFormat.format(amount)
                }
            }
            holder.itemView.img_more.tag=position
            holder.itemView.img_more.setOnClickListener {
                var count=holder.itemView.txt_count.text.toString().toInt()
                count += 1
                var price =0.0
                if(extraItem.total_price!=null ){
                    price =extraItem.total_price.toString().toDouble()
                }
                else {
                    price=extraItem.item_price!!.toDouble()

                }

                val pos=it.tag
                holder.itemView.txt_count.text=count.toString()
                quantity.comquantityChanged(price,1, pos as Int)
                var amount=count*price
               holder. itemView.order_dish_amount.text=context.getString(R.string.pound_symbol)+decimalFormat.format(amount)
            }
            holder.itemView.img_delete.tag=position
            holder.itemView.img_delete.setOnClickListener {
                val pos=it.tag
                quantity.comDeleteClicked(it, pos as Int,extraitems)
            }

        }
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