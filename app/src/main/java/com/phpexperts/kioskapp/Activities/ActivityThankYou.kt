package com.phpexperts.kioskapp.Activities

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.phpexperts.kioskapp.Models.User
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.DroidPrefs
import kotlinx.android.synthetic.main.layout_cancel_order.*
import kotlinx.android.synthetic.main.layout_thankyou.*

class ActivityThankYou : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_thankyou)
        val user=DroidPrefs.apply(this,"user", User::class.java) as User
        if(user.CustomerId!=null){
            txt_user_thank_you.text=getString(R.string.thank_you_txt)+" "+user.user_name
        }
        else {

        }

        setSpan("45.00 min.")

    }
    fun setSpan(type:String){
        val ss = SpannableString(getString(R.string.order_serving_time))
        val index=ss.indexOf(type)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
//                Toast.makeText(this@CancelOrderActivity,type,Toast.LENGTH_SHORT).show()
//                showRedeemDialog()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.setColor(getColor(R.color.hint_color))
            }
        }
        ss.setSpan(clickableSpan, index, index+type.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        txt_order_time.text = ss
        txt_order_time.movementMethod = LinkMovementMethod.getInstance()
        txt_order_time.highlightColor = Color.TRANSPARENT

    }
}