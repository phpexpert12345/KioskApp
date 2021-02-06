package com.phpexperts.kioskapp.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ComValue {
    @SerializedName("error")
    @Expose
    private var error: String? = null

    @SerializedName("error_msg")
    @Expose
    private var error_msg: String? = null

    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("comboslot_id")
    @Expose
    private var comboslot_id: String? = null

    fun getSlot_Option_Name(): String? {
        return Slot_Option_Name
    }

    fun setSlot_Option_Name(slot_Option_Name: String?) {
        Slot_Option_Name = slot_Option_Name
    }

    @SerializedName("Slot_Option_Name")
    @Expose
    private var Slot_Option_Name: String? = null

    @SerializedName("Free_allowed")
    @Expose
    private var Free_allowed: String? = null

    @SerializedName("Free_Topping_Selection_allowed")
    @Expose
    private var Free_Topping_Selection_allowed: String? = null

    fun getError(): String? {
        return error
    }

    fun setError(error: String?) {
        this.error = error
    }

    fun getError_msg(): String? {
        return error_msg
    }

    fun setError_msg(error_msg: String?) {
        this.error_msg = error_msg
    }

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getComboslot_id(): String? {
        return comboslot_id
    }

    fun setComboslot_id(comboslot_id: String?) {
        this.comboslot_id = comboslot_id
    }

    fun getFree_allowed(): String? {
        return Free_allowed
    }

    fun setFree_allowed(free_allowed: String?) {
        Free_allowed = free_allowed
    }

    fun getFree_Topping_Selection_allowed(): String? {
        return Free_Topping_Selection_allowed
    }

    fun setFree_Topping_Selection_allowed(free_Topping_Selection_allowed: String?) {
        Free_Topping_Selection_allowed = free_Topping_Selection_allowed
    }

    fun getComboSectionValueItems(): List<ComValueItem?>? {
        return ComboSectionValueItems
    }

    fun setComboSectionValueItems(comboSectionValueItems: List<ComValueItem?>?) {
        ComboSectionValueItems = comboSectionValueItems
    }

    @SerializedName("ComboSectionValueItems")
    @Expose
    private var ComboSectionValueItems: List<ComValueItem?>? = null
}