package com.phpexperts.kioskapp.Models

import androidx.room.*

@Dao
interface ToppingDao {
    @Insert(onConflict= OnConflictStrategy.IGNORE)
    fun Insert(toppingItems: ToppingItems)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun Update(toppingItems: ToppingItems)
    @Query("Select * from topping_items where item_name=:item_name order by topping_name")
    fun getToppingsbyItem(item_name :String):List<ToppingItems>
    @Query("Delete from topping_items where topping_id=:topping_id")
    fun DeleteTopping(topping_id : Int)
    @Query("Delete from topping_items where item_name=:item_name")
    fun DeleteToppingByItemName(item_name: String)
    @Query("Delete from topping_items")
    fun DeleteAll()
}