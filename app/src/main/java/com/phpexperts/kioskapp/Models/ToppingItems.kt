package com.phpexperts.kioskapp.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "topping_items", indices = arrayOf(Index(value = ["topping_name", "item_id"], unique = true)))
class ToppingItems {
    @ColumnInfo(name = "topping_name")
    @SerializedName("topping_name")
    var topping_name:String?=null
    @ColumnInfo(name="topping_price")
    @SerializedName("topping_price")
    var topping_price :String?=null
    @ColumnInfo(name = "item_name")
    @SerializedName("item_name")
    var item_name :String?=null
    @ColumnInfo(name = "topping_id")
    @SerializedName("topping_id")
    var topping_id :Int=0
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    var id=0
    @ColumnInfo(name="item_id")
    @SerializedName("item_id")
    var item_id:String?=null

}