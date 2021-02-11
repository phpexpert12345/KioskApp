package com.phpexperts.kioskapp.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.phpexperts.kioskapp.Models.GuestUser
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.Apis
import com.phpexperts.kioskapp.Utils.DroidPrefs
import com.phpexperts.kioskapp.Utils.KioskApplication
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import kotlinx.android.synthetic.main.customer_info.*
import kotlinx.android.synthetic.main.layout_cancel_order.*
import org.json.JSONObject

class CustomerInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customer_info)
KioskApplication.finish_activity=false
        btn_guest.setOnClickListener{
            if(edit_name.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.name_error), Toast.LENGTH_SHORT).show();
            }

            else {
//                Login()
                val guestUser=GuestUser()
                guestUser.name=edit_name.text.toString()
                guestUser.phone=edit_phone.text.toString()
                DroidPrefs.apply(this,"guest",guestUser)
                startActivity(Intent(this@CustomerInfoActivity, OrderTypeActivity::class.java))
                finish()
            }
        }
        setSpan("Click Here")
    }
    fun setSpan(type: String) {
        val ss = SpannableString(getString(R.string.click_here))
        val index = ss.indexOf(type)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@CustomerInfoActivity,LoginActivity::class.java))
//                Toast.makeText(this@CancelOrderActivity,type,Toast.LENGTH_SHORT).show()
//                showRedeemDialog()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.setColor(getColor(R.color.color_orange))
            }
        }
        ss.setSpan(clickableSpan, index, index + type.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        txt_login_txt.text = ss
        txt_login_txt.movementMethod = LinkMovementMethod.getInstance()
        txt_login_txt.highlightColor = Color.TRANSPARENT

    }

    override fun onResume() {
        super.onResume()
        if(KioskApplication.finish_activity){
            finish()
        }

    }

}