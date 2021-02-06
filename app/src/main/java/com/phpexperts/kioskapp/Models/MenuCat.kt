package com.phpexperts.kioskapp.Models

class MenuCat {
    var menu_category_time_enable:String?=null
    var menu_time_open:String?=null
    var menu_time_close:String?=null
    var Combo_Available:String?=null
    var Favorites_display:String?=null
    var id:Int=0
    var Category_ID:Int=0
    var restaurant_id:String?=null
    var sc_obj_id:String?=null
    var category_name:String?=null
    var category_description:String?=null
    var category_img:String?=null
    var subItemsRecord:ArrayList<SubItemRecords>?=null
    var ComboList:ArrayList<ComboLists>?=null
}