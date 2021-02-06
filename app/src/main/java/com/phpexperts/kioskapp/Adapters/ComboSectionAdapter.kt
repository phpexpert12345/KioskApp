package com.phpexperts.kioskapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phpexperts.kioskapp.Models.ComboSection
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.SelectSec
import kotlinx.android.synthetic.main.extra_item_radio.view.*

class ComboSectionAdapter( comboSections: List<ComboSection>,sec:SelectSec):RecyclerView.Adapter<ComboSectionAdapter.ComboSectionViewHolder>() {
    var comboSections=comboSections
    var sec = sec
    var selected_pos = -1
    class ComboSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboSectionViewHolder {
        return ComboSectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.extra_item_radio, parent, false))
    }

    override fun getItemCount(): Int {
return comboSections.size
    }

    override fun onBindViewHolder(holder: ComboSectionViewHolder, position: Int) {
        val comboSection=comboSections.get(position)
        holder.itemView.txt_extra_price.visibility = View.GONE
        holder.itemView.txt_extra_name.text = comboSection.getDeal_slot_name()
        holder.itemView.img_extra_radio.isChecked = selected_pos==position
        holder.itemView.relative_extra.tag=position
        holder.itemView.relative_extra.setOnClickListener {
            val pos=it.tag
            selected_pos= pos as Int
            sec.getClickSec(pos)
            notifyDataSetChanged()
        }
    }
}