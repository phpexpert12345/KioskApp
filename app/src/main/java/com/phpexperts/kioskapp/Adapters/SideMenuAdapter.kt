package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.Glide
import com.phpexperts.kioskapp.Models.MenuCat
import com.phpexperts.kioskapp.Models.MySingleton
import com.phpexperts.kioskapp.Models.SideMenuItem
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.layout_side_menu_item.view.*

class SideMenuAdapter(sidemenuItems : ArrayList<MenuCat>, menuClicked: MenuClicked) : RecyclerView.Adapter<SideMenuAdapter.SideMenuViewHolder>() {
    var selected_position=0
    var context: Context?=null
    var sideMenuItems=sidemenuItems
    var imageLoader :ImageLoader?= null
    interface MenuClicked{
        fun Clicked(view: View, position: Int)
    }
    var menuClicked=menuClicked

    class SideMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
fun bind(sideMenuItem: MenuCat, context: Context){
    itemView.txt_side_menu_title.text=sideMenuItem.category_name
    Glide.with(context).load(sideMenuItem.category_img).placeholder(R.drawable.ic_palceholder).into(itemView.img_side_menu )
    itemView.linear_side_menu.tag=adapterPosition

}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SideMenuViewHolder {
        context=parent.context
        imageLoader=MySingleton.getInstance(context!!)!!.imageLoader
        return SideMenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_side_menu_item,null))
    }

    override fun getItemCount(): Int {
        return sideMenuItems.size
    }

    override fun onBindViewHolder(holder: SideMenuViewHolder, position: Int) {
        holder.bind(sideMenuItems.get(position), context!!)
        holder.itemView.linear_side_menu.tag=position
holder.itemView.linear_side_menu.setOnClickListener {
    selected_position=position
    val pos=it.tag
    menuClicked.Clicked(it, pos as Int)
    notifyDataSetChanged()
}

        if (selected_position==position) {
            holder.itemView.linear_side_menu.background=context!!.getDrawable(R.drawable.background_grey_side)
            holder.itemView.txt_side_menu_title.setTextColor(context!!.getColor(R.color.white))

        } else {
            holder.itemView.linear_side_menu.background=context!!.getDrawable(R.drawable.edit_background)
            holder.itemView.txt_side_menu_title.setTextColor(context!!.getColor(R.color.black))

        }
    }
}