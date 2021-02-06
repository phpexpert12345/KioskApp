package com.phpexperts.kioskapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.Models.SingleCom
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.layout_com_item.view.*

class SingleComAdapter(singleComs:List<SingleCom>):RecyclerView.Adapter<SingleComAdapter.SingleComViewHolder>() {
    var singleComs=singleComs
    class SingleComViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleComViewHolder {
return SingleComViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_com_item,parent,false))
    }

    override fun getItemCount(): Int {
return singleComs.size
    }

    override fun onBindViewHolder(holder: SingleComViewHolder, position: Int) {
        val singleCom = singleComs[position]
        holder.itemView.tv_menu_item_sec.setText(singleCom.section)
        holder.itemView.tv_menu_item_size.setText(singleCom.size)
        if (singleCom.tops!!.contains("_")) {
            if (singleCom.tops!!.startsWith("_")) {
                singleCom.tops = singleCom.tops!!.substring(1)
            }
            val extra: Array<String> = singleCom.tops!!.split("_").toTypedArray()
            if (extra.size > 0) {
                var stringBuilder = StringBuilder()
                for (i in extra.indices) {
                    stringBuilder.append("+").append(extra[i])
                    stringBuilder.append("\n")
                }
                if (stringBuilder.length > 0) {
                    stringBuilder = stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("\n"))
                    holder.itemView.tv_menu_item_extra.setText(stringBuilder.toString())
                }
            }
        }
    }
}