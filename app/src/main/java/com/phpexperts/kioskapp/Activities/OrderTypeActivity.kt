 package com.phpexperts.kioskapp.Activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
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
        val buttonClick = AlphaAnimation(1f, 0.3f)
        linear_dine.setOnClickListener {
           it.startAnimation(buttonClick)
            startActivity(Intent(this,MenuActivity::class.java).putExtra("type", "Dine In"))
            finish()
        }
        linear_takeaway.setOnClickListener {
            it.startAnimation(buttonClick)
            startActivity(Intent(this,MenuActivity::class.java).putExtra("type", "Take away"))
            finish()
        }

    }
}