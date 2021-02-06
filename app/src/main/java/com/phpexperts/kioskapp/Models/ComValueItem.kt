package com.phpexperts.kioskapp.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ComValueItem {
    @SerializedName("Combo_Slot_ItemID")
    @Expose
    private var Combo_Slot_ItemID: Int? = null

    @SerializedName("Combo_Slot_ItemName")
    @Expose
    private var Combo_Slot_ItemName: String? = null

    @SerializedName("ItemID")
    @Expose
    private var ItemID: Int? = null

    @SerializedName("FoodItemSizeID")
    @Expose
    private var FoodItemSizeID: String? = null

    fun getCombo_Slot_ItemID(): Int? {
        return Combo_Slot_ItemID
    }

    fun setCombo_Slot_ItemID(combo_Slot_ItemID: Int?) {
        Combo_Slot_ItemID = combo_Slot_ItemID
    }

    fun getCombo_Slot_ItemName(): String? {
        return Combo_Slot_ItemName
    }

    fun setCombo_Slot_ItemName(combo_Slot_ItemName: String?) {
        Combo_Slot_ItemName = combo_Slot_ItemName
    }

    fun getItemID(): Int? {
        return ItemID
    }

    fun setItemID(itemID: Int?) {
        ItemID = itemID
    }

    fun getFoodItemSizeID(): String? {
        return FoodItemSizeID
    }

    fun setFoodItemSizeID(foodItemSizeID: String?) {
        FoodItemSizeID = foodItemSizeID
    }

    fun getCombo_Topping_Allow(): String? {
        return Combo_Topping_Allow
    }

    fun setCombo_Topping_Allow(combo_Topping_Allow: String?) {
        Combo_Topping_Allow = combo_Topping_Allow
    }

    @SerializedName("Combo_Topping_Allow")
    @Expose
    private var Combo_Topping_Allow: String? = null
}