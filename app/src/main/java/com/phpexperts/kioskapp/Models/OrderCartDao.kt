 package com.phpexperts.kioskapp.Models
import androidx.room.*

@Dao
interface OrderCartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun Insert(orderCartItem: OrderCartItem)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun Update(orderCartItem: OrderCartItem)
    @Query("Select * from order_cart_item order by item_name")
    fun getCartItems():List<OrderCartItem>
    @Query("delete from order_cart_item where item_name=:name and top_ids=:top_ids ")
    fun DeleteCartitem(name:String,top_ids:String)
    @Query("delete from order_cart_item")
    fun DeleteAll()
    @Query("select * from order_cart_item where item_name=:name")
    fun getOrderItem(name:String):OrderCartItem
    @Query("select * from order_cart_item where item_name=:name and top_ids=:top_ids")
    fun getOrderItemids(name:String,top_ids:String):OrderCartItem

}