package com.phpexperts.kioskapp.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexperts.kioskapp.Adapters.OrderConfirmedAdapter
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.payment_confirmed.*

class PaymentConfirmedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_confirmed)
        setAdapter()

    }
    fun setAdapter(){
        val orderConfirmedAdapter=OrderConfirmedAdapter()
        val linearLayoutManager=LinearLayoutManager(this)
         recyler_order.adapter=orderConfirmedAdapter
        recyler_order.layoutManager=linearLayoutManager

    }
}