package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.Models.ComValue
import com.phpexperts.kioskapp.Models.ComValueItem
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.SelectSecItem
import kotlinx.android.synthetic.main.extra_item_radio.view.*
import kotlinx.android.synthetic.main.layout_sec_item.view.*

class ComValueAdapter(comvalues:List<ComValue>,comValueClicked: ComValueClicked,context:Context):RecyclerView.Adapter<ComValueAdapter.ComValueViewHolder>() {
    var comValues=comvalues
    var comValueClicked=comValueClicked
    var context=context
    interface ComValueClicked {
        fun ComClicked(subItemsRecord: ComValueItem?, comslot_id: Int, sec_: String?)

        fun comValClicked(comValue: ComValue?)
    }
    class ComValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComValueViewHolder {
        return ComValueViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_sec_item, parent, false))
    }

    override fun getItemCount(): Int {
return comValues.size
    }

    override fun onBindViewHolder(holder: ComValueViewHolder, position: Int) {
        val comValue = comValues.get(position)
        holder.itemView.txt_sec_text.setText(comValue.getSlot_Option_Name())
        holder.itemView.relative_sec.setTag(position)
        holder.itemView.relative_sec.setOnClickListener {
            val pos=it.tag
            comValueClicked.comValClicked(comValues[pos as Int])
            if(holder.itemView.recyler_sec_item.visibility==View.GONE){
                holder.itemView.recyler_sec_item.visibility=View.VISIBLE
            }
            else{
                holder.itemView.recyler_sec_item.visibility=View.GONE
            }
        }
        val linearLayoutManager = LinearLayoutManager(context)
        val comValueAdapter = ComValueItemAdapter(comValue.getComboSectionValueItems() as List<ComValueItem>, object : SelectSecItem {
            override fun getClickSecComboItem(subItemsRecord: ComValueItem) {
                comValueClicked.ComClicked(subItemsRecord, comValue.getComboslot_id()!!.toInt(), comValue.getSlot_Option_Name())
            }

        })
        holder.itemView.recyler_sec_item.setAdapter(comValueAdapter)
        holder.itemView.recyler_sec_item.setLayoutManager(linearLayoutManager)
    }
}