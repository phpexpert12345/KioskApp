package com.phpexperts.kioskapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.Models.ComValueItem
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.SelectSecItem
import kotlinx.android.synthetic.main.extra_item_radio.view.*

class ComValueItemAdapter(comValueItems:List<ComValueItem>, selectSecItem: SelectSecItem):RecyclerView.Adapter<ComValueItemAdapter.ComValueItemViewHolder>() {
    var comValueItems=comValueItems
    var selectSecItem=selectSecItem
    var select_pos = -1
    class ComValueItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComValueItemViewHolder {
        return ComValueItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.extra_item_radio, parent, false))
    }

    override fun getItemCount(): Int {
return comValueItems.size
    }

    override fun onBindViewHolder(holder: ComValueItemViewHolder, position: Int) {
        val comValueItem = comValueItems.get(position)
        if(select_pos==position){
            holder.itemView.img_extra_radio.isChecked=true
        }
        else{
            holder.itemView.img_extra_radio.isChecked=false
        }
        holder.itemView.txt_extra_price.visibility=View.GONE
        holder.itemView.txt_extra_name.text=comValueItem.getCombo_Slot_ItemName()
        holder.itemView.relative_extra.tag=position
        holder.itemView.relative_extra.setOnClickListener {
            val pos=it.tag
            select_pos= pos as Int
            selectSecItem.getClickSecComboItem(comValueItems.get(pos))
            notifyDataSetChanged()

        }


    }
}