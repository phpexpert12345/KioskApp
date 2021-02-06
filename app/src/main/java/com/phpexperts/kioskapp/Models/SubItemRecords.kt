package com.phpexperts.kioskapp.Models

import java.io.Serializable

class SubItemRecords :Serializable {
    var ItemID:Int=0
    var RestaurantPizzaID:Int=0
    var RestaurantCategoryID:String?=null
    var RestaurantPizzaItemName:String?=null
    var food_tax_applicable:String?=null
    var extraavailable:String?=null
    var ResPizzaDescription:String?=null
    var Food_NameNo:String?=null
    var additives_Description:String?=null
    var Dish_Ingredients:String?=null
    var Food_Type:String?=null
    var Food_Type_Non:String?=null
    var Food_Popular:String?=null
    var Food_Spicy:String?=null
    var MidFood_Spicy:String?=null
    var VeryFood_Spicy:String?=null
    var GreenFood_Spicy:String?=null
    var food_Icon:String?=null
    var RestaurantPizzaItemSizeName:String?=null
    var RestaurantPizzaItemOldPrice:String?=null
    var Discount_Food_Amount:String?=null
    var RestaurantPizzaItemPrice:String?=null
    var sizeavailable:String?=null
    var is_com:Boolean=false

}