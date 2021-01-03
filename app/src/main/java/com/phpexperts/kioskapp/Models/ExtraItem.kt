package com.phpexperts.kioskapp.Models

class ExtraItem(extra_name:String,extra_image:String) {
    var extra_item_name:String?=null
    var extra_item_image:String?=null
    init {
        extra_item_image=extra_image
        extra_item_name=extra_name
    }
}