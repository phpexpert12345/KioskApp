 package com.phpexperts.kioskapp.Activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.phpexperts.kioskapp.R
import kotlinx.android.synthetic.main.layout_order_type.*

class OrderTypeActivity :AppCompatActivity() {
    var name =""
    var phone=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_order_type)
        name= intent.getStringExtra("name").toString()
        phone=intent.getStringExtra("phone").toString()
        linear_dine.setOnClickListener {
            ObjectAnimator.ofFloat(linear_dine, "translationY", 100f).apply {
                duration = 2000
                start()
            }
            startActivity(Intent(this,MenuActivity::class.java).putExtra("name", name).putExtra("phone",phone).putExtra("type", "Dine In"))
            finish()
        }
        linear_takeaway.setOnClickListener {
            ObjectAnimator.ofFloat(linear_takeaway, "translationY", 100f).apply {
                duration = 2000
                start()
            }
            startActivity(Intent(this,MenuActivity::class.java).putExtra("name", name).putExtra("phone",phone).putExtra("type", "Take away"))
            finish()
        }

    }
}