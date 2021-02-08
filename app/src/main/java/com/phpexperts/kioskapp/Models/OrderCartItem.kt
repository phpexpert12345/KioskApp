package com.phpexperts.kioskapp.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Entity(tableName = "order_cart_item", indices = arrayOf(Index(value = ["item_name"],unique = true)))
class OrderCartItem  :Serializable{
    @ColumnInfo(name="item_name")
    @SerializedName("item_name")
    var item_name:String?=null
    @ColumnInfo(name="item_price")
    @SerializedName("item_price")
    var item_price :String?=null
    @ColumnInfo(name="quantity")
    @SerializedName("quantity")
    var quantity:Int=0
    @ColumnInfo(name="item_size_type")
    @SerializedName("item_size_type")
    var item_size_type:String?=null
    @ColumnInfo(name="item_image")
    @SerializedName("item_image")
    var item_image:String?=null
    @ColumnInfo(name="id")
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    var id :Int=0
    @ColumnInfo(name="total_price")
    @SerializedName("total_price")
    var total_price:String?=null
    @ColumnInfo(name="item_size_id")
    @SerializedName("item_size_id")
    var item_size_id:String?=null
    @ColumnInfo(name = "is_com")
    @SerializedName("is_com")
    var com:Boolean=false
    @ColumnInfo(name = "item_id")
    @SerializedName("item_id")
    var item_id:Int=0
    @ColumnInfo(name = "deal_id")
    @SerializedName("deal_id")
    var deal_id=0

}