package com.phpexperts.kioskapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.select_card_type.*

class SelectCardType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_card_type)
        visa.setOnClickListener{
            startActivity(Intent(this,CardDetailsActivity::class.java))
        }
        mastercard.setOnClickListener{
            startActivity(Intent(this,CardDetailsActivity::class.java))
        }
        express.setOnClickListener{
            startActivity(Intent(this,CardDetailsActivity::class.java))
        }
        android_pay.setOnClickListener{
            startActivity(Intent(this,CardDetailsActivity::class.java))
        }
        apple_pay.setOnClickListener{
            startActivity(Intent(this,CardDetailsActivity::class.java))
        }

    }
}