package com.phpexperts.kioskapp.Models

class CartItem(name:String, image:String) {
    var cart_item_name:String?=null
    var cart_item_image:String?=null
    init {
        cart_item_image=image
        cart_item_name=name
    }
}