package com.phpexperts.kioskapp.Utils

import com.phpexperts.kioskapp.Models.SubExtraItemsRecord

interface ExtraClick {
    fun ExtraClicked(food:String,food_price:String,food_id:Int,extraitems:ArrayList<SubExtraItemsRecord>)
}