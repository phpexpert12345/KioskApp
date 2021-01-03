package com.phpexperts.kioskapp.Models

import java.io.Serializable

class SubExtraItemsRecord :Serializable {
    var ExtraID :Int=0
    var error:String?=null
    var error_msg:String?=null
    var Food_Addons_Name:String?=null
    var Food_Price_Addons:String?=null
    var Free_Topping_Selection_allowed:String?=null
    var selected:Boolean=false
//    "ExtraID": 6827,
//    "error": "0",
//    "error_msg": "success",
//    "Food_Addons_Name": "mit Artischocken",
//    "Food_Price_Addons": "1.50",
//    "Free_Topping_Selection_allowed": ""
}