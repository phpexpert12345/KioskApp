package com.phpexperts.kioskapp.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ComboSection {
    @SerializedName("error")
    @Expose
    private var error: String? = null

    @SerializedName("error_msg")
    @Expose
    private var error_msg: String? = null

    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("deal_slot_name")
    @Expose
    private var deal_slot_name: String? = null

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

    fun getDeal_slot_name(): String? {
        return deal_slot_name
    }

    fun setDeal_slot_name(deal_slot_name: String?) {
        this.deal_slot_name = deal_slot_name
    }

    fun getComboSectionValue(): List<ComValue?>? {
        return ComboSectionValue
    }

    fun setComboSectionValue(comboSectionValue: List<ComValue?>?) {
        ComboSectionValue = comboSectionValue
    }

    @SerializedName("ComboSectionValue")
    @Expose
    private var ComboSectionValue: List<ComValue?>? = null
}