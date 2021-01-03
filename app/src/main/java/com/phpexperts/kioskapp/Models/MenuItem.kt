package com.phpexperts.kioskapp.Models

class MenuItem(name:String, image:String,price:String, discounted_price :String) {
    var menu_item_name:String?=null
    var menu_item_image:String?=null
    var menu_item_price:String?=null
    var menu_item_discounted_price :String?=null
    init {
        menu_item_image=image
        menu_item_name=name
        menu_item_price=price
        menu_item_discounted_price=discounted_price
    }
}