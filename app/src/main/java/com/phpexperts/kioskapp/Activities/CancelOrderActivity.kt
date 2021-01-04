 package com.phpexperts.kioskapp.Activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import bbota01z.bbota01z.bbota01z.e
import com.phpexperts.kioskapp.Adapters.CancelOrderAdapter
import com.phpexperts.kioskapp.Models.ExtraItem
import com.phpexperts.kioskapp.Models.OrderCartItem
import com.phpexperts.kioskapp.Models.User
import com.phpexperts.kioskapp.Models.UserInfo
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.Apis
import com.phpexperts.kioskapp.Utils.CartDatabase
import com.phpexperts.kioskapp.Utils.DroidPrefs
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.callable.ConnectionTokenProvider
import com.stripe.stripeterminal.callable.TerminalListener
import com.stripe.stripeterminal.log.LogLevel
import com.stripe.stripeterminal.model.external.ConnectionTokenException
import com.stripe.stripeterminal.model.external.Reader
import kotlinx.android.synthetic.main.dialog_redeem.*
import kotlinx.android.synthetic.main.dialog_redeem.view.*
import kotlinx.android.synthetic.main.layout_cancel_order.*
import kotlinx.android.synthetic.main.layout_order_item.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CancelOrderActivity :AppCompatActivity(), KioskVolleyService.KioskResult, ConnectionTokenProvider {
    var extraItems=ArrayList<ExtraItem>()
    var orderCartItems =ArrayList<OrderCartItem>()
    var total_price =0.0
    var type =""
    var decimalFormat= DecimalFormat("##.00")
    var payment_key=""
    var loyalty_points =""
    var alertDialog:AlertDialog?=null
    var deliveryChargeValue:String?=null
    var VatTax:String?=null
    var discountOfferPrice:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cancel_order)
        getCartItemsfromDataBase()
        getRoyaltyPoints()
        getPaymentKey()
        getServiceTax()
        getDiscount()
        txt_redeem_points.setOnClickListener {
            if(!loyalty_points.equals("")){
                RedeemLoyaltyPoints(loyalty_points)
            }
            else {
Toast.makeText(this,getString(R.string.loyalty_txt),Toast.LENGTH_SHORT).show()
            }
        }

        txt_place_order.setOnClickListener {
            EmptyCart()
            val intent=Intent(this, ActivityThankYou::class.java)
            startActivity(intent)
            finish()
        }
        img_cart_back.setOnClickListener {
            finish()
        }
        img_empty_cart.setOnClickListener {
            val builder=AlertDialog.Builder(this).setMessage(getString(R.string.empty_cart)).setPositiveButton(getString(R.string.ok)) { dialog, which ->
                dialog.dismiss()
                EmptyCart()
            }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            builder.create().show()


        }

            type = intent.getStringExtra("type")!!
            when (type) {
                "Dine In" -> setDeliveryType(0)
                "Take away" -> setDeliveryType(1)
            }
//        getOrderDetails()

            linear_dine.setOnClickListener {
                type="Dine In"
                setDeliveryType(0)
            }
            linear_takeaway.setOnClickListener {
                type="Take away"
                setDeliveryType(1)
            }
            txt_apply_coupon.setOnClickListener {
                showRedeemDialog()
            }
//        setSpan("Redeem")
        }
        fun setAdapters() {
            txt_no_cart_item.visibility=View.GONE
            recyler_new_orders.visibility=View.VISIBLE
            val cartDatabase = CartDatabase.getDataBase(this)
            val toppingDao = cartDatabase!!.ToppingDao()
            if(total_price>0.0){
                total_price =0.0
            }
            for (orderCartitem in orderCartItems) {
                if(orderCartitem.total_price!=null){
                    total_price+=orderCartitem.total_price!!.toDouble()*orderCartitem.quantity
                }
                else {
                    total_price += orderCartitem.item_price!!.toDouble() * orderCartitem.quantity
                }



            }
            if (total_price > 0.0) {
                txt_total_count.text =
                    getString(R.string.pound_symbol) + decimalFormat.format(total_price)
            }
            val linearLayoutManager = LinearLayoutManager(this)
            val cancelOrderAdapter =
                CancelOrderAdapter(orderCartItems, this, object : CancelOrderAdapter.Quantity {
                    override fun quantityChanged(price: Double, type: Int) {
                        when (type) {
                            0 -> total_price -= price
                            1 -> total_price += price
                        }

                        txt_total_count.text =
                            getString(R.string.pound_symbol) + decimalFormat.format(total_price)
                    }

                    override fun DeleteClicked(view: View, pos: Int) {
                        showDeleteDialog(orderCartItems.get(pos))
                    }
                })
            recyler_new_orders.adapter = cancelOrderAdapter
            recyler_new_orders.layoutManager = linearLayoutManager
        }
    fun showDeleteDialog(orderCartItem: OrderCartItem){
        val builder =AlertDialog.Builder(this).setMessage(getString(R.string.are_you_sure)+" "+orderCartItem.item_name+" "+getString(R.string.from_your_cart))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            DeleteCartItem(orderCartItem)
            dialog.dismiss() }
        builder.setNegativeButton(getString(R.string.cancel)){
            dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()

    }
    fun DeleteCartItem(orderCartItem: OrderCartItem){
        val cartDatabase=CartDatabase.getDataBase(this)
        val orderCartDao =cartDatabase!!.OrderCartDao()
        val toppingDao=cartDatabase.ToppingDao()
        orderCartDao!!.DeleteCartitem(orderCartItem.item_name.toString())
        toppingDao!!.DeleteToppingByItemName(orderCartItem.item_name.toString())
        getCartItemsfromDataBase()

    }
    fun EmptyCart(){
        val cartDatabase=CartDatabase.getDataBase(this)
        val orderCartDao =cartDatabase!!.OrderCartDao()
        val toppingDao=cartDatabase.ToppingDao()
        orderCartDao!!.DeleteAll()
        toppingDao!!.DeleteAll()
        getCartItemsfromDataBase()
    }


        override fun onResult(response: JSONObject, type: String) {
            if(type.equals("stripe_payment")){
                if(response.getString("response").equals("Error")){
                    Toast.makeText(this,response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }
            else if(type.equals("payment_key")){
                val stripe_publishKey=response.getString("stripe_publishKey")
                if(stripe_publishKey!=null){
                    payment_key=stripe_publishKey

                }


            }
            else if(type.equals("royalty_point")){
                Log.i("response", response.toString())
                if(response.has("Total_Loyalty_points")){
                   loyalty_points=response.getString("Total_Loyalty_points")
                    if(loyalty_points!=null){
                        txt_points.setText(getString(R.string.loyalty_points)+" "+loyalty_points)
                    }
                }

            }
            else if(type.equals("redeem_loyalty")){
                Log.i("response", response.toString())
                if(response.has("success")){
                    val success=response.getInt("success")
                    if(success==1){
                        Toast.makeText(this,response.getString("success_msg"),Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if(type.equals("apply_coupon")){
                Log.i("response", response.toString())
                getCartItemsfromDataBase()
                if(response .has("error")){
                    val error=response.getInt("error")
                    if(error==2){
                        Toast.makeText(this,response.getString("error_msg"),Toast.LENGTH_SHORT).show()
                    }
                }
                alertDialog!!.dismiss()
            }
            else if(type.equals("service_charge")){
                Log.i("response", response.toString())
                deliveryChargeValue=response.getString("deliveryChargeValue")
                VatTax=response.getString("VatTax")
                val delivery_charge=deliveryChargeValue.toString().toDouble()
                val vat_tax=VatTax.toString().toDouble()
                if(total_price>0.0) {
                    total_price = total_price + delivery_charge + vat_tax
                    txt_total_count.text = getString(R.string.pound_symbol) + decimalFormat.format(total_price)
                }

            }
            else if(type.equals("discount")){
                Log.i("res", response.toString())
                if(response.has("discountOfferPrice")){
                    discountOfferPrice=response.getString("discountOfferPrice")
                    val offer_price=discountOfferPrice.toString().toDouble()
                    if(offer_price>0.0){
                        if(total_price>0.0) {
                            total_price = total_price - offer_price
                             txt_total_count.text = getString(R.string.pound_symbol) + decimalFormat.format(total_price)
                        }
                    }
                }
            }

        }

        fun setSpan(type: String) {
            val ss = SpannableString(getString(R.string.redeem_txt))
            val index = ss.indexOf(type)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
//                Toast.makeText(this@CancelOrderActivity,type,Toast.LENGTH_SHORT).show()
                    showRedeemDialog()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.setColor(getColor(R.color.color_orange))
                }
            }
            ss.setSpan(clickableSpan, index, index + type.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            txt_redeem.text = ss
            txt_redeem.movementMethod = LinkMovementMethod.getInstance()
            txt_redeem.highlightColor = Color.TRANSPARENT

        }

        fun showRedeemDialog() {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_redeem, null)
            builder.setView(view)
             alertDialog = builder.create()
            alertDialog!!.show()
            view.txt_back.setOnClickListener {
                alertDialog!!.dismiss()
            }
            view.txt_apply.setOnClickListener {
                if (view.edit_coupon_code.text.toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_coupon_code), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    ApplyCouponCode(view.edit_coupon_code.text.toString())
//                    Toast.makeText(this, view.edit_coupon_code.text.toString(), Toast.LENGTH_SHORT)
//                        .show()

                    alertDialog!!.dismiss()
                }
            }
        }
    fun ApplyCouponCode(couponCode :String){
        val kioskVolleyService=KioskVolleyService()
        kioskVolleyService.context=this
        kioskVolleyService.kioskResult=this
        kioskVolleyService.type="apply_coupon"
        kioskVolleyService.url=Apis.BASE_URL+"couponCode.php"
        val userInfo=DroidPrefs.get(this,"userinfo", UserInfo::class.java)
        val params=HashMap<String,String>()
        params.put("api_key", userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("subTotal", total_price.toString())
        params.put("CouponCode", couponCode)
        params.put("resid", userInfo.resid.toString())
        params.put("CouponCodePrice", "")
        params.put("couponCodeType", type)
        kioskVolleyService.CreateStringRequest(params)
    }

        fun getCartItemsfromDataBase() {
            val cartDatabase = CartDatabase.getDataBase(this)
            val cartDao = cartDatabase!!.OrderCartDao()
            orderCartItems = cartDao!!.getCartItems() as ArrayList<OrderCartItem>
            if(orderCartItems.size>0) {
                setAdapters()
            }
            else {
                recyler_new_orders.visibility=View.GONE
                txt_no_cart_item.visibility=View.VISIBLE
                txt_total_count.text=getString(R.string.pound_symbol)+"0.0"
            }
        }

        fun setDeliveryType(type: Int) {
            when (type) {
                0 -> {
                    linear_dine.setBackgroundResource(R.drawable.background_filled_green)
                    img_dine.setImageResource(R.drawable.ic_chicken)
                    txt_dine_in.setTextColor(getColor(R.color.white))
                    txt_takeaway.setTextColor(getColor(R.color.black))
                    img_takeaway.setImageResource(R.drawable.ic_takeaway_black)
                    linear_takeaway.setBackgroundResource(R.drawable.background_order_type)

                }
                1 -> {
                    linear_dine.setBackgroundResource(R.drawable.background_order_type)
                    img_dine.setImageResource(R.drawable.ic_chicken_black)
                    txt_dine_in.setTextColor(getColor(R.color.black))
                    txt_takeaway.setTextColor(getColor(R.color.white))
                    img_takeaway.setImageResource(R.drawable.ic_takeaway)
                    linear_takeaway.setBackgroundResource(R.drawable.background_filled_green)
                }

            }
        }


    fun getRoyaltyPoints(){
        val volleyService=KioskVolleyService()
        volleyService.context=this
        volleyService.url=Apis.BASE_URL+"phpexpert_customer_loyalty_point.php"
        volleyService.type="royalty_point"
        volleyService.kioskResult=this
        val user = DroidPrefs.get(this,"user", User::class.java)
        val params=HashMap<String,String>()
        if(user.CustomerId!=null){
            params.put("CustomerId", user.CustomerId.toString())
        }
        volleyService.CreateStringRequest(params)
    }
    fun getPaymentKey(){
        val volleyService=KioskVolleyService()
        volleyService.context=this
        volleyService.url=Apis.BASE_URL+"phpexpert_payment_key.php"
        volleyService.type="payment_key"
        volleyService.kioskResult=this
        val userInfo=DroidPrefs.get(this,"userinfo",UserInfo::class.java)
        val params=HashMap<String, String>()
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("api_key", userInfo.api_key.toString())
        volleyService.CreateStringRequest(params)
    }
    fun SubmitPayment(){
        val volleyService=KioskVolleyService()
        volleyService.url=Apis.BASE_URL+"phpexpert_payment_intent_generate.php"
        volleyService.kioskResult=this
        volleyService.context=this
        volleyService.type="stripe_payment"
        val userInfo= DroidPrefs.get(this,"userinfo", UserInfo::class.java)
        val params=HashMap<String,String>()
        params.put("api_key",userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("amount", total_price.toString())
        params.put("currency", userInfo.customer_currency.toString())
        params.put("stripeToken", payment_key)
        params.put("description", "testing stripe payments")
        volleyService.CreateStringRequest(params)

    }

    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
        try {
            callback.onSuccess(payment_key)
        }
        catch(e:Exception){
            callback.onFailure(ConnectionTokenException("Failed to fetch Connection token", e))
        }

//       callback.onSuccess()
    }
    fun RedeemLoyaltyPoints(loyalty_points:String){
        val volleyService=KioskVolleyService()
        volleyService.type="redeem_loyalty"
        volleyService.context=this
        volleyService.kioskResult=this
        volleyService.url=Apis.BASE_URL+"phpexpert_customer_loyalty_point_redeem.php"
        val user=DroidPrefs.get(this,"user", User::class.java)
        val params=HashMap<String,String>()
        params.put("CustomerId", user.CustomerId.toString())
        params.put("loyltPnts", loyalty_points)
        params.put("TotalFoodCostAmount", total_price.toString())
        volleyService.CreateStringRequest(params)
    }
    fun PlaceOrder(){
         val volleyService=KioskVolleyService()
        volleyService.type="place_order"
        volleyService.context=this
        volleyService.kioskResult=this
        volleyService.url=Apis.BASE_URL+" .php"
        val userInfo=DroidPrefs.get(this, "userinfo", UserInfo::class.java)
        val simpledateformat=SimpleDateFormat("dd/MM/yyyy")
        val calendar =Calendar.getInstance()
        val current_date=simpledateformat.format(calendar.time)
        val params=HashMap<String,String>()
        params.put("api_key", userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("payment_type","Card")
        params.put("order_price", total_price.toString())
        params.put("subTotalAmount", total_price.toString())
        params.put("delivery_time", current_date)
        params.put("deliveryCharge",deliveryChargeValue.toString())
        params.put("order_type",type)
        params.put("SalesTaxAmount",VatTax.toString())
        volleyService.CreateStringRequest(params)
//        params.put("")
    }
    fun getServiceTax(){
        val kioskVolleyService=KioskVolleyService()
        kioskVolleyService.url=Apis.BASE_URL+"ServiceChargetGet.php"
        kioskVolleyService.context=this
        kioskVolleyService.kioskResult=this
        kioskVolleyService.type="service_charge"
        val userInfo=DroidPrefs.get(this,"userinfo", UserInfo::class.java)
        val params=HashMap<String,String>()
        params.put("subTotal", total_price.toString())
        params.put("api_key", userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("resid",userInfo.resid.toString())
        params.put("Order_Type", type)
        kioskVolleyService.CreateStringRequest(params)
    }
    fun getDiscount(){
        val kioskVolleyService=KioskVolleyService()
        kioskVolleyService.url=Apis.BASE_URL+"discountGet.php"
        kioskVolleyService.context=this
        kioskVolleyService.kioskResult=this
        kioskVolleyService.type="discount"
        val userInfo=DroidPrefs.get(this,"userinfo", UserInfo::class.java)
        val params=HashMap<String,String>()
        params.put("subTotal", total_price.toString())
        params.put("api_key", userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("resid", userInfo.resid.toString())
        params.put("Order_Type",type)
        kioskVolleyService.CreateStringRequest(params)

    }



}