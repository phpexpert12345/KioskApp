package com.phpexperts.kioskapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexperts.kioskapp.Models.BannerItem
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.layout_banner.view.*
import kotlinx.android.synthetic.main.layout_extra_item.view.*

class BannerAdapter(banneritems:ArrayList<BannerItem>, context: Context) :RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    var banneritems=banneritems
    var context=context
    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(bannerItem: BannerItem, context:Context){
            Glide.with(context).load(bannerItem.app_banner_img).into(itemView.img_banner)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
         return BannerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_banner,parent,false))
    }

    override fun getItemCount(): Int {
       return banneritems.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
holder.bind(banneritems.get(position),context)
    }
}