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
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.Apis
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import kotlinx.android.synthetic.main.customer_info.*
import kotlinx.android.synthetic.main.layout_cancel_order.*
import org.json.JSONObject

class CustomerInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customer_info)

        btn_guest.setOnClickListener{
            if(edit_name.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.name_error), Toast.LENGTH_SHORT).show();
            }
            else if(edit_phone.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.phone_error), Toast.LENGTH_SHORT).show();
            }
            else if(edit_phone.text.toString().length<10){
                Toast.makeText(this,getString(R.string.phone_validation_error), Toast.LENGTH_SHORT).show();
            }
            else {
//                Login()
                startActivity(Intent(this@CustomerInfoActivity, OrderTypeActivity::class.java).putExtra("name", edit_name.text.toString()).putExtra("phone",edit_phone.text.toString()))
                finish()
            }
        }
        setSpan("Click Here")
    }
    fun setSpan(type: String) {
        val ss = SpannableString(getString(R.string.login_txt))
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
        txt_login.text = ss
        txt_login.movementMethod = LinkMovementMethod.getInstance()
        txt_login.highlightColor = Color.TRANSPARENT

    }

}