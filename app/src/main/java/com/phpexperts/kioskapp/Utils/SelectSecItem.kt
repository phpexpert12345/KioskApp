package com.phpexperts.kioskapp.Utils

import com.phpexperts.kioskapp.Models.ComValueItem

interface SelectSecItem {
    fun getClickSecComboItem(subItemsRecord: ComValueItem)
}