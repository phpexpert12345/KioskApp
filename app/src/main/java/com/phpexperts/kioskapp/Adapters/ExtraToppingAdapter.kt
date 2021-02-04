package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.Models.SubExtraItemsRecord
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.ExtraClick
import kotlinx.android.synthetic.main.extra_item_radio.view.*

class ExtraToppingAdapter(extraitems:ArrayList<SubExtraItemsRecord>,context:Context,extraClick: ExtraClick) :RecyclerView.Adapter<ExtraToppingAdapter.ExtraToppingViewHolder>(){
    var extraitems=extraitems
    var context=context
    var selected_pos = -1
    val extraClick=extraClick;
    public class ExtraToppingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtraToppingViewHolder {
        return ExtraToppingViewHolder(LayoutInflater.from(context).inflate(R.layout.extra_item_radio,null))
    }

    override fun getItemCount(): Int {
        return extraitems.size
    }

    override fun onBindViewHolder(holder: ExtraToppingViewHolder, position: Int) {
        val subExtraItemsRecord=extraitems.get(position)
        if(selected_pos==position){

            holder.itemView.img_extra_radio.setChecked(true);
        }
        else{
            holder.itemView.img_extra_radio.setChecked(false)
        }
        holder.itemView.relative_extra.tag=position
        holder.itemView.relative_extra.setOnClickListener(View.OnClickListener {
            val pos=it.tag
            selected_pos=pos as Int
            extraClick.ExtraClicked(extraitems.get(pos).Food_Addons_Name!!,extraitems.get(pos).Food_Price_Addons!!,extraitems.get(pos).ExtraID,extraitems)
            notifyDataSetChanged()
        })
        holder.itemView.txt_extra_name.text=subExtraItemsRecord.Food_Addons_Name
        holder.itemView.txt_extra_price.text=context.getString(R.string.pound_symbol)+subExtraItemsRecord.Food_Price_Addons
    }
}