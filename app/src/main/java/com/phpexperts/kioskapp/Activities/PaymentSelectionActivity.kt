package com.phpexperts.kioskapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.payment_selection.*

class PaymentSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_selection)
        linear_pound.setOnClickListener {
startActivity(Intent(this,CorrectOrderActivity::class.java))
        }
        linear_card.setOnClickListener{
            startActivity(Intent(this,CorrectOrderActivity::class.java))
        }

    }
}