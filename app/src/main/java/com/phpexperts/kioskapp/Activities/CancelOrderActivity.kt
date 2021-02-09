 package com.phpexperts.kioskapp.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phpexperts.kioskapp.Adapters.CancelOrderAdapter
import com.phpexperts.kioskapp.Models.*
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.*
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.callable.*
import com.stripe.stripeterminal.log.LogLevel
import com.stripe.stripeterminal.model.external.*
import kotlinx.android.synthetic.main.dialog_loyalty.view.*
import kotlinx.android.synthetic.main.dialog_redeem.view.*
import kotlinx.android.synthetic.main.dialog_redeem.view.txt_back
import kotlinx.android.synthetic.main.layout_cancel_order.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


 class CancelOrderActivity :AppCompatActivity(), KioskVolleyService.KioskResult, ConnectionTokenProvider,TerminalListener {
    var extraItems=ArrayList<ExtraItem>()
    var orderCartItems =ArrayList<OrderCartItem>()
    var total_price =0.0
    var order_price=0.0
    var type =""
    var decimalFormat= DecimalFormat("##.00")
    var payment_key=""
    var loyalty_points =""
    var alertDialog:AlertDialog?=null
    var alertDialogLoyalty:AlertDialog?=null
    var deliveryChargeValue:String?=null
    var VatTax:String?=null
    var discountOfferPrice:String?=null
    var  cartDatabase:CartDatabase?=null
    var cartDao :OrderCartDao?=null
    var toppingDao:ToppingDao?=null
    var CouponCodePrice:String?=null
    var coupon_discount:Double=0.0
    var loyalty_price:Double=0.0
    var delivery_charge:Double=0.0
    var offer_price:Double=0.0
    var tax:Double=0.0
    var loyalty_discount:String?=null
     var item_Id=""
     var quantity=""
     var Price=""
     var strsizeid=""
     var extraItemID=""
     var subTotalAmount=""
     var FoodCosts=""
     var extraItemId1=""
     var extraItemId2=""
     var extra_toppings=""
     var SeviceFeesValue=""
     var ServiceFees=""
     var ServiceFeesType=""
     var PackageFeesType=""
     var PackageFees=""
     var PackageFeesValue=""
     var SalesTaxAmount=""
     var current_time=""
     lateinit var progressDialog:ProgressDialog
     private var mealID = ""
     private var mealquqntity = ""
     private var mealPrice = ""
     private var mealItemExtra = ""
     private var mealItemOption = ""


     override fun onResume() {
         super.onResume()
         if(KioskApplication.finish_activity){
             finish()
         }
     }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cancel_order)
        KioskApplication.finish_activity=false
        getCartItemsfromDataBase()
        val user=DroidPrefs.get(this,"user", User::class.java)

        val couponApplied=DroidPrefs.get(this,"coupon_applied",CouponApplied::class.java)
        if(couponApplied!=null){
            if(couponApplied.is_coupon_applied){
                txt_apply_coupon.visibility=View.GONE
                CouponCodePrice=couponApplied.coupon_discount;
                coupon_discount=CouponCodePrice!!.toDouble()
                UpdateData()
            }
            else {
                txt_apply_coupon.visibility=View.VISIBLE
            }
        }
        val loyaltyApplied=DroidPrefs.get(this,"loyalty_applied", LoyaltyApplied::class.java)
        if(loyaltyApplied!=null){
            if(loyaltyApplied.is_loyalty_applied){
                linear_royalty_points.visibility=View.GONE
                loyalty_discount=loyaltyApplied.loyalty_discount
                loyalty_price=loyalty_discount!!.toDouble()
                UpdateData()
            }
            else {
                linear_royalty_points.visibility=View.VISIBLE
            }
        }
        if(user.CustomerId!=null) {
            getRoyaltyPoints()
        }
        else {
            linear_royalty_points.visibility=View.GONE
        }
        getPaymentKey()
        getServiceTax()
        getDiscount()
        txt_redeem_points.setOnClickListener {
            if(!loyalty_points.equals("")){
                showLoyaltyDialog()
            }
            else {
Toast.makeText(this,getString(R.string.loyalty_txt),Toast.LENGTH_SHORT).show()
            }
        }

        txt_place_order.setOnClickListener {
            if (!Terminal.isInitialized()) {
                Terminal.initTerminal(this, LogLevel.INFO, this, this)
            }
            CollectPaymentTerminal()
//            Connect()
//            Connect()

//            EmptyCart()
//            val intent=Intent(this, ActivityThankYou::class.java)
//            startActivity(intent)
//            finish()
        }
        img_cart_back.setOnClickListener {
            KioskApplication.finish_activity=true
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
            relative_cart_items.visibility=View.VISIBLE
            relative_order_bottom.visibility=View.VISIBLE
            relative_redeem.visibility=View.VISIBLE
            layout_order_type.visibility=View.VISIBLE
            linear_no_cart.visibility=View.GONE

             toppingDao = cartDatabase!!.ToppingDao()
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
                UpdateData()
            }
            val linearLayoutManager = LinearLayoutManager(this)
            val cancelOrderAdapter =
                CancelOrderAdapter(orderCartItems, this, object : CancelOrderAdapter.Quantity {
                    override fun quantityChanged(price: Double, type: Int,pos:Int) {
                        when (type) {

                            0 -> {
                                total_price -= price
                                val cartItem=cartDao!!.getOrderItem(orderCartItems.get(pos).item_name!!)
                                var quantity=cartItem.quantity
                                quantity -= 1
                                cartItem.quantity=quantity
                                UpdateCart(cartItem)

//
                            }

                            1 -> {
                                total_price += price
                                val cartItem=cartDao!!.getOrderItem(orderCartItems.get(pos).item_name!!)
                                var quantity=cartItem.quantity
                                quantity += 1
                                cartItem.quantity=quantity
                                UpdateCart(cartItem)
                            }

                        }

                        UpdateData()


                    }

                    override fun DeleteClicked(view: View, pos: Int) {
                        showDeleteDialog(orderCartItems.get(pos))
                    }

                    override fun comquantityChanged(price: Double, type: Int, pos: Int) {
                        val com_list = DroidPrefs.get(this@CancelOrderActivity, "com_list", String::class.java)
                        if (com_list != null) {
                            val gson = Gson()
                            val listType = object : TypeToken<List<ComItemList?>?>() {}.type
                            val comItemLists = gson.fromJson<List<ComItemList>>(com_list, listType)
                            if (comItemLists != null) {
if(comItemLists.size>0){
    when (type) {

        0 -> {
            total_price -= price

            var quantity=comItemLists.get(pos).quantity
            quantity -= 1
            comItemLists.get(pos).quantity=quantity
            val gson = Gson()
            val com = gson.toJson(comItemLists)
            DroidPrefs.apply(this@CancelOrderActivity, "com_list", com)
            getCartItemsfromDataBase()

//
        }

        1 -> {
            total_price += price
            var quantity=comItemLists.get(pos).quantity
            quantity += 1
            comItemLists.get(pos).quantity=quantity
            val com = gson.toJson(comItemLists)
            DroidPrefs.apply(this@CancelOrderActivity, "com_list", com)
            getCartItemsfromDataBase()
        }

    }

    UpdateData()

}
                            }
                        }
                    }

                    override fun comDeleteClicked(view: View, pos: Int,extraitems:ArrayList<OrderCartItem>) {
                        showComDeleteDialog(extraitems.get(pos))
                    }

                })
            recyler_new_orders.adapter = cancelOrderAdapter
            recyler_new_orders.layoutManager = linearLayoutManager
        }

    fun UpdateCart(cartItem: OrderCartItem){
        val database=CartDatabase.getDataBase(this)
        val cartDao=database!!.OrderCartDao()
        cartDao!!.Update(cartItem)
        getCartItemsfromDataBase()
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
     fun showComDeleteDialog(orderCartItem: OrderCartItem){
        val builder =AlertDialog.Builder(this).setMessage(getString(R.string.are_you_sure)+" "+orderCartItem.item_name+" "+getString(R.string.from_your_cart))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            val com_list = DroidPrefs.get(this@CancelOrderActivity, "com_list", String::class.java)
            if (com_list != null) {
                val gson = Gson()
                val listType = object : TypeToken<List<ComItemList?>?>() {}.type
                val comItemLists:MutableList<ComItemList> =
                        gson.fromJson<List<ComItemList>>(com_list, listType).toMutableList()
                if (comItemLists != null) {
                    if(comItemLists.size>0){
                        var index=-1
                        for(i in 0 until comItemLists.size){
                            if(comItemLists.get(i).deal_id!!.toInt()==orderCartItem.deal_id){
                                index=i

                            }
                        }
                        if(index>=0) {
                            comItemLists.removeAt(index)
                        }
                    }
                    val com = gson.toJson(comItemLists)
                    DroidPrefs.apply(this@CancelOrderActivity, "com_list", com)
                    getCartItemsfromDataBase()


                }

            }
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
        orderCartDao!!.DeleteCartitem(orderCartItem.item_name.toString(),orderCartItem.top_ids)
        if(orderCartItem.top_ids.contains(",")){
            val tops=orderCartItem.top_ids.split(",")
            for(top in tops){
                toppingDao!!.DeleteTopping(top.toInt())
            }
        }
        else{
            if(!orderCartItem.top_ids.equals("")){
                toppingDao!!.DeleteTopping(orderCartItem.top_ids.toInt())
            }

        }

        getCartItemsfromDataBase()

    }
    fun EmptyCart(){
        val cartDatabase=CartDatabase.getDataBase(this)
        val orderCartDao =cartDatabase!!.OrderCartDao()
        val toppingDao=cartDatabase.ToppingDao()
        orderCartDao!!.DeleteAll()
        toppingDao!!.DeleteAll()
        DroidPrefs.getDefaultInstance(this).clearkey("com_list")
        getCartItemsfromDataBase()
    }


        override fun onResult(response: JSONObject, type: String) {
            if(type.equals("stripe_payment")){
                if(response.getString("response").equals("Error")){
                    Toast.makeText(this,response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }
            else if(type.equals("payment_key")){
                if(response.has("token_secret")) {
                    val token_secret = response.getJSONObject("token_secret")
                    if (token_secret.length() > 0) {
                        payment_key = token_secret.getString("secret")
                    }
                }
//                val stripe_publishKey=response.getString("stripe_publishKey")
//                if(stripe_publishKey!=null){
//                    payment_key=stripe_publishKey
//
//                }


            }
            else if(type.equals("royalty_point")){
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
                    else {
                        val Total_Loyalty_amount=response.getString("Total_Loyalty_amount")
                        getCartItemsfromDataBase()
                        loyalty_price=Total_Loyalty_amount.toDouble()
                        linear_royalty_points.visibility=View.GONE
                        val loyaltyApplied=DroidPrefs.get(this,"loyalty_applied",LoyaltyApplied::class.java)
                        if(loyaltyApplied!=null){
                            if(!loyaltyApplied.is_loyalty_applied){
                                loyaltyApplied.is_loyalty_applied=true
                                loyaltyApplied.loyalty_discount=Total_Loyalty_amount
                                DroidPrefs.apply(this,"loyalty_applied",loyaltyApplied)
                            }
                        }
                        UpdateData()
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
                    else if(error==0){
                       CouponCodePrice=response.getString("CouponCodePrice")
                        val couponApplied=DroidPrefs.get(this,"coupon_applied",CouponApplied::class.java)
                        if(couponApplied!=null){
                            if(!couponApplied.is_coupon_applied){
                                couponApplied.is_coupon_applied=true
                                couponApplied.coupon_discount=CouponCodePrice
                                DroidPrefs.apply(this,"coupon_applied", couponApplied)
                                txt_apply_coupon.visibility=View.GONE
                                coupon_discount=CouponCodePrice!!.toDouble()
                                UpdateData()
                            }
                        }
                    }
                }
                alertDialog!!.dismiss()
            }
            else if(type.equals("service_charge")){
                Log.i("response", response.toString())
//                deliveryChargeValue=response.getString("deliveryChargeValue")
                VatTax=response.getString("VatTax")
//                delivery_charge=deliveryChargeValue.toString().toDouble()
                SeviceFeesValue=response.getString("SeviceFeesValue")
                ServiceFees=response.getString("ServiceFees")
                ServiceFeesType=response.getString("ServiceFeesType")
                PackageFeesType=response.getString("PackageFeesType")
                PackageFees=response.getString("PackageFees")
                PackageFeesValue=response.getString("PackageFeesValue")
                SalesTaxAmount=response.getString("SalesTaxAmount")
                tax=VatTax.toString().toDouble()
                UpdateData()


            }
            else if(type.equals("discount")){
                Log.i("res", response.toString())
                if(response.has("discountOfferPrice")){
                    discountOfferPrice=response.getString("discountOfferPrice")
                     offer_price=discountOfferPrice.toString().toDouble()
                    if(offer_price>0.0){
                        if(total_price>0.0) {
                           UpdateData()
                        }
                    }
                }
            }
            else if(type.equals("place_order")){
                progressDialog.dismiss()

                    if(response.has("success")){
                        val success=response.getInt("success")
                        when(success){
                            1->{
                                val order_no=response.getString("order_no")
                                val restaurant_name=response.getString("restaurant_name")
                                val restoid=response.getString("restoid")
                                val ordPrice=response.getString("ordPrice")
                                val order_type=response.getString("order_type")
                                val intent=Intent(this,ActivityThankYou::class.java)
                                intent.putExtra("order_no",order_no)
                                intent.putExtra("restaurant_name",restaurant_name)
                                intent.putExtra("order_type",order_type)
                                intent.putExtra("ordPrice",ordPrice)
                                intent.putExtra("restoid",restoid)
                                intent.putExtra("order_time",current_time)

                                startActivity(intent)

                            }
                        }




                }
                Log.i("response", response.toString())
            }
            else if(type.equals("guest_place_order")){
                progressDialog.dismiss()

                Log.i("response", response.toString())
            }
            else if(type.equals("terminal")){
                progressDialog.dismiss()
                Log.i("terminal", response.toString())
                if(response.has("response")){
                    val res=response.getString("response")
                    if(res.equals("Error",true)){
                        Toast.makeText(this,response.getString("message"),Toast.LENGTH_SHORT).show()
                    }
                    else{
                        if(response.has("client_secret")){
                            val client_secret=response.getJSONObject("client_secret")
                            if(client_secret!=null){
                                if(client_secret.has("id")){
                                    val id=client_secret.getString("id")
                                    CaptureIntent(id)
                                }
                            }
                        }

                    }
                }
                else{
                    Toast.makeText(this,response.getString("error_msg"),Toast.LENGTH_SHORT).show()
                }

            }
            else if(type.equals("capture_Intent")){
                Log.i("response", response.toString())
                progressDialog.dismiss()
                PlaceOrder()

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
    fun UpdateData(){
        if(total_price>0.0) {
            order_price = total_price - coupon_discount - loyalty_price + delivery_charge + tax - offer_price
            txt_total_count.text =
                    getString(R.string.pound_symbol) + decimalFormat.format(order_price)
            if (total_price > 0.0) {
                subTotalAmount = java.lang.String.valueOf(total_price)
                FoodCosts = subTotalAmount
            }
        }
        else {
            txt_total_count.text =
                    getString(R.string.pound_symbol) +"0.0"
        }

    }
    fun showLoyaltyDialog(){
        var loyalty=getString(R.string.loyalty_points_txt).toString()
        loyalty=loyalty.replace("%",loyalty_points)
        val view=layoutInflater.inflate(R.layout.dialog_loyalty,null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()
        view.txt_loyalty_count.text=loyalty
        val user_points=loyalty_points.toDouble()
        view.txt_apply_loyalty.setOnClickListener {
            if(view.edit_loyalty.text.toString().isEmpty()){
                Toast.makeText(this, getString(R.string.please_loyalty_points), Toast.LENGTH_SHORT).show()
            }
            else if(!view.edit_loyalty.text.toString().equals("0",true)) {
                val points=view.edit_loyalty.text.toString().toDouble()

                if(points>0 && points<user_points){
                    RedeemLoyaltyPoints(points.toString())
                   dialog.dismiss()
                }


            }
            else {
                 Toast.makeText(this, getString(R.string.please_loyalty_points), Toast.LENGTH_SHORT).show()
            }
        }
    }

        fun showRedeemDialog() {
            val view = layoutInflater.inflate(R.layout.dialog_redeem, null)
            val dialog = BottomSheetDialog(this)
            view.txt_apply.setOnClickListener {
                if (view.edit_coupon_code.text.toString().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_coupon_code), Toast.LENGTH_SHORT)
                            .show()
                } else {
                    ApplyCouponCode(view.edit_coupon_code.text.toString())
//                    Toast.makeText(this, view.edit_coupon_code.text.toString(), Toast.LENGTH_SHORT)
//                        .show()

                    dialog.dismiss()
                }
            }
            dialog.setContentView(view)
            dialog.show()


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
            cartDatabase = CartDatabase.getDataBase(this)
             cartDao = cartDatabase!!.OrderCartDao()
            toppingDao=cartDatabase!!.ToppingDao()
            orderCartItems = cartDao!!.getCartItems() as ArrayList<OrderCartItem>
            //  tvTotalFoodCost.setText("+".concat(pound.concat("" + String.format("%.2f", totalPrice))));
            //    tvTotalFoodCost.setText("+".concat(pound.concat("" +String.valueOf(totalPrice))));
            // getDiscount();
            val meal_quatity = java.lang.StringBuilder()
            val meal_id = java.lang.StringBuilder()
            val meal_price = java.lang.StringBuilder()
            val meal_extra_opt = java.lang.StringBuilder()
            val meal_item_extra = java.lang.StringBuilder()
            var toatl_price = 0.0
            total_price=0.0
            val com_list = DroidPrefs.get(this, "com_list", String::class.java)
            if (com_list != null) {
                val gson = Gson()
                val listType = object : TypeToken<List<ComItemList?>?>() {}.type
                val comItemLists = gson.fromJson<List<ComItemList>>(com_list, listType)
                if (comItemLists != null) {
                    for (comItemList in comItemLists) {
                        val item_ids: Array<String> = comItemList.ItemID!!.split(",").toTypedArray()
                        val food_ids: Array<String> = comItemList.FoodItemSizeID!!.split(",").toTypedArray()
                        val slot_id: Array<String> = comItemList.comboslot_id!!.split(",").toTypedArray()
                        val comslot_item: Array<String> = comItemList.Combo_Slot_ItemID!!.split(",").toTypedArray()
                        if (meal_item_extra.length > 0) {
                            meal_item_extra.append("=")
                        }
                        if (meal_quatity.length > 0) {
                            meal_quatity.append(",")
                        }
                        if (meal_price.length > 0) {
                            meal_price.append(",")
                        }
                        if (meal_id.length > 0) {
                            meal_id.append(",")
                        }
                        if (meal_extra_opt.length > 0) {
                            meal_extra_opt.append("=")
                        }
                        meal_quatity.append(comItemList.quantity)
                        meal_price.append(comItemList.price)
                        meal_id.append(comItemList.deal_id)
                        if (item_ids.size > 0) {
                            for (i in item_ids.indices) {
                                if (meal_extra_opt.length > 0) {
                                    meal_extra_opt.append(",")
                                }
                                meal_extra_opt.append(slot_id[i])
                                meal_extra_opt.append("_")
                                meal_extra_opt.append(comslot_item[i])
                                meal_extra_opt.append("_")
                                meal_extra_opt.append(item_ids[i])
                                meal_extra_opt.append("_")
                                meal_extra_opt.append(food_ids[i])
                                val com_tops: Array<String> = comItemList.combo_top_id!!.split(",").toTypedArray()
                                if (com_tops.size > 0) {
                                    for (j in com_tops.indices) {
                                        if (i == j) {
                                            val top_item = com_tops[j]
                                            if (top_item.contains("_")) {
                                                val s = top_item.split("_".toRegex()).toTypedArray()
                                                for (t in s.indices) {
                                                    if (meal_item_extra.length > 0) {
                                                        meal_item_extra.append(",")
                                                    }
                                                    meal_item_extra.append(s[t])
                                                    meal_item_extra.append("_")
                                                    meal_item_extra.append(slot_id[i])
                                                    meal_item_extra.append("_")
                                                    meal_item_extra.append(comslot_item[i])
                                                    meal_item_extra.append("_")
                                                    meal_item_extra.append(item_ids[i])
                                                    meal_item_extra.append("_")
                                                    meal_item_extra.append(food_ids[i])
                                                    meal_item_extra.append("_")
                                                    meal_item_extra.append(slot_id[i])
                                                    meal_item_extra.append("_")
                                                    meal_item_extra.append(comItemList.deal_id)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }




                    mealID = meal_id.toString()
                    mealquqntity = meal_quatity.toString()
                    mealPrice = meal_price.toString()
                    mealItemOption = meal_extra_opt.toString()
                    mealItemExtra = meal_item_extra.toString()
                    Log.i("reason", "$mealID, $mealquqntity, $mealPrice, $mealItemOption, $mealItemExtra")
                        val orderCartItem=OrderCartItem();
                        orderCartItem.quantity=comItemList.quantity
                        orderCartItem.item_name=comItemList.combo_name
                        orderCartItem.item_size_type=comItemList.sec_value
                        orderCartItem.total_price=comItemList.price
                        orderCartItem.item_price=comItemList.price
                        orderCartItem.quantity=comItemList.quantity
                        orderCartItem.item_image=comItemList.com_sec
                        orderCartItem.item_size_id=comItemList.combo_top
                        orderCartItem.com=true
                        orderCartItem.deal_id=comItemList.deal_id!!.toInt()
                        orderCartItems.add(orderCartItem)

//                        raviCartModles.add(RaviCartModle(comItemList.combo_name, comItemList.combo_name, comItemList.sec_value, comItemList.com_sec, "", comItemList.combo_top, comItemList.price, java.lang.String.valueOf(comItemList.quantity), comItemList.combo_desc, 1))
                    }
                }
            }
            if(orderCartItems.size>0) {

                setAdapters()
            }
            else {
                relative_cart_items.visibility=View.GONE
                relative_order_bottom.visibility=View.GONE
                relative_redeem.visibility=View.GONE
                layout_order_type.visibility=View.GONE
                DroidPrefs.getDefaultInstance(this).clearkey("loyalty_applied")
                DroidPrefs.getDefaultInstance(this).clearkey("coupon_applied")
                linear_no_cart.visibility=View.VISIBLE

            }
            val itemId = StringBuilder()
            val quant = StringBuilder()
            val price_total = StringBuilder()
            val strsize_all = StringBuilder()
            var extra_all = StringBuilder()
            var extraItemid3 = StringBuilder()
            val extra_name = StringBuilder()
            if(orderCartItems.size>0){

                for(order in orderCartItems) {
                    if (!order.com){
                        itemId.append(order.item_id)
                    itemId.append(",")
                    quant.append(order.quantity)
                    quant.append(",")
                    price_total.append(order.total_price!!.toDouble() * order.quantity)
                    price_total.append(",")
                    strsize_all.append(order.item_size_id)
                    strsize_all.append(",")
                    val topping_list = toppingDao!!.getToppingsbyItem(order.item_name!!)
                    if (topping_list.size > 0) {
                        for (topping in topping_list) {
                            var extra_id: String
                            extra_id = topping.topping_id.toString()
                            extraItemid3.append(extra_id.trim())
                            extraItemid3.append(",")
                            extra_all.append(order.item_id.toString() + "_" + order.item_size_id + "_" + topping.topping_id.toString().trim())
                            extra_all.append(",")
                            extra_name.append("+" + topping.topping_name)

                        }
                        extra_name.append("_")
                        extra_all.append("_")
                        if (extra_all.length > 0) {
                            extra_all = extra_all.deleteCharAt(extra_all.lastIndexOf(","))
                        }
                        if (extraItemid3.length > 0) {
                            extraItemid3 = extraItemid3.deleteCharAt(extraItemid3.lastIndexOf(","))
                        }
                        extraItemid3.append("_")
                    }
                }

                    }
                    if (itemId.length > 0) {
                        if(itemId.contains(",")) {
                            item_Id = itemId.deleteCharAt(itemId.lastIndexOf(",")).toString()
                        }
                        else{
                            item_Id=itemId.toString()
                        }
                    }
                    if (quant.length > 0) {
                        if(quant.contains(",")) {
                            quantity = quant.deleteCharAt(quant.lastIndexOf(",")).toString()
                        }
                    }
                    if (price_total.length > 0) {
                        if(price_total.contains(",")) {
                            Price = price_total.deleteCharAt(price_total.lastIndexOf(",")).toString()
                        }
                        else{
                            Price=price_total.toString()
                        }
                    }
                    if (strsize_all.length > 0) {
                        if(strsize_all.contains(",")) {
                            strsizeid = strsize_all.deleteCharAt(strsize_all.lastIndexOf(",")).toString()
                        }
                        else{
                            strsizeid=strsize_all.toString()
                        }
                    }
                    if (extra_all.length > 0) {
                        if(extra_all.contains("_")) {
                            extraItemID = extra_all.deleteCharAt(extra_all.lastIndexOf("_")).toString()
                        }
                        else{
                            extraItemID=extra_all.toString()
                        }
                    }

                    if (extraItemid3.length > 0) {
                        if(extraItemid3.contains("_")) {
                            extraItemId1 = extraItemid3.deleteCharAt(extraItemid3.lastIndexOf("_")).toString()
                        }
                        else{
                            extraItemId1=extraItemid3.toString()
                        }
                    }

                    if (extra_name.length > 0) {
                        if(extra_name.contains("_")) {
                            extraItemId2 = extra_name.deleteCharAt(extra_name.lastIndexOf("_")).toString()

                        }
                        else{
                            extraItemId2=extra_name.toString()
                        }
                        extra_toppings = extraItemId2

                    }

                extraItemId2=Util.ConvertToBase64(extraItemId2)
                Log.i("reason",item_Id+" "+quantity+" "+Price+" "+strsizeid+" "+extraItemID+" "+subTotalAmount+' '+FoodCosts+" "+extraItemId1+" "+extraItemId2);
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
        volleyService.url=Apis.BASE_URL+"phpexpert_payment_generate_token.php"
        Log.i("url",volleyService.url!!)
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
     fun Connect(){
         progressDialog=Util.showProgressDialog(this,"Initiating Payment...")
         val config = DiscoveryConfiguration(0, DeviceType.CHIPPER_2X, true)
         Terminal.getInstance().discoverReaders(config,object: DiscoveryListener {
             override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
                 val firstReader = readers.first()
                 Terminal.getInstance().connectReader(firstReader,object: ReaderCallback {
                     override fun onSuccess(reader: Reader) {
                         progressDialog.dismiss()
                         Log.i("respose",reader.serialNumber!!)
                         CollectPaymentTerminal()

                     }

                     override fun onFailure(e: TerminalException) {
                         progressDialog.dismiss()
                         Log.i("respose","failure "+e.errorMessage)
                     }
                 })
             }
         },object: Callback {
             override fun onSuccess() {

                 Log.i("respose","success callack")
             }

             override fun onFailure(e: TerminalException) {
                 Log.i("respose","failure callack")
             }
         })

     }
    fun PlaceOrder(){
//        val url = "https://www.lieferadeal.de/WebAppAPI/phpexpert_payment_android_submit.php?api_key=" + prefsHelper.getPref(Constants.API_KEY).toString() + "&lang_code=" + prefsHelper.getPref(Constants.LNG_CODE).toString() + "&payment_transaction_paypal=&itemId=" + item_Id.toString() + "&Quantity=" + quantity.toString() + "&Price=" + Price.toString() + "&strsizeid=" + strsizeid.toString() + "&extraItemID=" + extraItemID.toString() + "&CustomerId=" + CustomerId.toString() + "&CustomerAddressId=" + addressId.toString() + "&payment_type=Cash&order_price=" + order_price.toString() + "&subTotalAmount=" + subTotalAmount.toString() + "&delivery_date=" + delivery_date.toString() + "&delivery_time=" + delivery_time.toString() + "&instructions=" + instructions.toString() + "&deliveryCharge=" + deliveryChargeValue.toString() + "&CouponCode=" + CouponCode.toString() + "&CouponCodePrice=" + CouponCodePrice.toString() + "&couponCodeType=" + couponCodeType.toString() + "&SalesTaxAmount=" + SalesTaxAmount.toString() + "&order_type=" + order_type.toString() + "&SpecialInstruction=" + SpecialInstruction.toString() + "&extraTipAddAmount=" + extraTipAddAmount.toString() + "&RestaurantNameEstimate=" + RestaurantNameEstimate.toString() + "&discountOfferDescription=" + discountOfferDescription.toString() + "&discountOfferPrice=" + discountOfferPrice.toString() + "&RestaurantoffrType=" + RestaurantoffrType.toString() + "&ServiceFees=" + ServiceFees.toString() + "&PaymentProcessingFees=" + PaymentProcessingFees.toString() + "&deliveryChargeValueType=" + deliveryChargeValueType.toString() + "&ServiceFeesType=" + ServiceFeesType.toString() + "&PackageFeesType=" + PackageFeesType.toString() + "&PackageFees=" + PackageFees.toString() + "&WebsiteCodePrice=" + WebsiteCodePrice.toString() + "&WebsiteCodeType=" + WebsiteCodeType.toString() + "&WebsiteCodeNo=" + WebsiteCodeNo.toString() + "&preorderTime=" + preorderTime.toString() + "&VatTax=" + VatTax.toString() + "&GiftCardPay=" + GiftCardPay.toString() + "&WalletPay=" + WalletPay.toString() + "&loyptamount=" + loyptamount.toString() + "&table_number_assign=" + table_number_assign.toString() + "&customer_country=" + customer_country.toString() + "&group_member_id=" + group_member_id.toString() + "&loyltPnts=" + loyltPnts.toString() + "&branch_id=" + branch_id.toString() + "&FoodCosts=" + FoodCosts.toString() + "&getTotalItemDiscount=" + getTotalItemDiscount.toString() + "&getFoodTaxTotal7=" + getFoodTaxTotal7.toString() + "&getFoodTaxTotal19=" + getFoodTaxTotal19.toString() + "&TotalSavedDiscount=" + TotalSavedDiscount.toString() + "&discountOfferFreeItems=" + discountOfferFreeItems.toString() + "&number_of_people=" + number_of_people.toString() + "&resid=" + restId.toString() + "&mealID=" + mealID.toString() + "&mealquqntity=" + mealquqntity.toString() + "&mealPrice=" + mealPrice.toString() + "&mealItemExtra=" + mealItemExtra.toString() + "&mealItemOption=" + mealItemOption.toString() + "&discountOfferFreeItems=" + discountOfferFreeItems.toString() + "&extraItemID1=" + extraItemId1.toString() + "&extraItemIDName1=" + extraItemId2
//        Log.i("reason", url)
        val userInfo=DroidPrefs.get(this, "userinfo", UserInfo::class.java)
        val user=DroidPrefs.get(this,"user", User::class.java)
        progressDialog=Util.showProgressDialog(this,"Placing order...")
         val volleyService=KioskVolleyService()
        volleyService.type="place_order"
        if(user.CustomerId!=null){

            volleyService.url=Apis.BASE_URL+"phpexpert_payment_android_submit.php"

        }
        else {

            volleyService.url=Apis.BASE_URL+"phpexpert_payment_android_submit_guest.php"
        }
        volleyService.context=this
        volleyService.kioskResult=this
        val simpledateformat=SimpleDateFormat("dd/MM/yyyy")
        val simple=SimpleDateFormat("HH:mm")
        val calendar =Calendar.getInstance()
        val current_date=simpledateformat.format(calendar.time)
         current_time=simple.format(calendar.time)
        val delivery_time=Util.ConvertToBase64(current_time)
        val params=HashMap<String,String>()
        params.put("api_key", userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("payment_type","Credit_Debit")
        params.put("order_price", order_price.toString())
        params.put("subTotalAmount", total_price.toString())
        params.put("delivery_date", current_date)
        params.put("deliveryCharge",delivery_charge.toString())
        params.put("order_type",type)
        params.put("SalesTaxAmount",VatTax.toString())
        params.put("itemId",item_Id)
        params.put("Quantity",quantity)
        params.put("Price",Price)
        params.put("strsizeid",strsizeid)
        params.put("extraItemID",extraItemID)
        if(user.CustomerId!=null) {
            params.put("CustomerId",user.CustomerId.toString())
            params.put("CustomerAddressId","0")
        }
        else {
            params.put("CustomerId", "0")
            params.put("CustomerAddressId","0")
        }
        params.put("delivery_time",delivery_time)
        params.put("loyltPnts",loyalty_price.toString())
        params.put("FoodCosts",FoodCosts)
        params.put("resid",userInfo.resid.toString())
        params.put("extraItemID1",extraItemId1.toString())
        params.put("extraItemIDName1",extraItemId2)
        params.put("instructions","")
        params.put("CouponCode", "")
        params.put("CouponCodePrice",coupon_discount.toString())
        params.put("couponCodeType","")
        params.put("SalesTaxAmount","0.0")
        params.put("order_type",type)
        params.put("SpecialInstruction","")
        params.put("extraTipAddAmount","0.0")
        params.put("RestaurantNameEstimate","")
        params.put("discountOfferDescription","")
        params.put("discountOfferPrice",offer_price.toString())
        params.put("RestaurantoffrType","")
        params.put("ServiceFees",ServiceFees)
        params.put("PackageFeesType",PackageFeesType)
        params.put("deliveryChargeValueType","")
        params.put("WebsiteCodePrice","")
        params.put("WebsiteCodeType","")
        params.put("WebsiteCodeNo","")
        params.put("preorderTime","")
        params.put("VatTax",VatTax.toString())
        params.put("payment_transaction_paypal","")
        params.put("mealID","")
        params.put("mealquqntity","")
        params.put("mealPrice","")
        params.put("mealItemExtra","")
        params.put("mealItemOption","")
        params.put("PaymentProcessingFees","")
        params.put("ServiceFeesType",ServiceFeesType)
        params.put("PackageFees",PackageFees)
        params.put("GiftCardPay","")
        params.put("WalletPay","")
        params.put("loyptamount","")
        params.put("table_number_assign","")
        params.put("customer_country","")
        params.put("group_member_id","")
        params.put("branch_id","")
        params.put("getTotalItemDiscount","")
        params.put("getFoodTaxTotal7","")
        params.put("getFoodTaxTotal19","")
        params.put("TotalSavedDiscount","")
        params.put("discountOfferFreeItems","")
        params.put("OrderTimeType","0")
        params.put("mealID",mealID)
        params.put("mealquqntity",mealquqntity)
        params.put("mealPrice",mealPrice)
        params.put("mealItemExtra",mealItemExtra)
        params.put("mealItemOption",mealItemOption)
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
     fun CollectPaymentTerminal(){
         val kioskVolleyService=KioskVolleyService()
         progressDialog=Util.showProgressDialog(this,"Collecting payment...")
         kioskVolleyService.url=Apis.BASE_URL+"phpexpert_payment_collect_Payment_Terminal.php"
         kioskVolleyService.context=this
         kioskVolleyService.kioskResult=this
         kioskVolleyService.type="terminal"
         val userInfo=DroidPrefs.get(this,"userinfo", UserInfo::class.java)
         val params=HashMap<String,String>()
         params.put("api_key", userInfo.api_key.toString())
         params.put("lang_code", userInfo.customer_default_langauge.toString())
         params.put("amount", order_price.toString())
         params.put("currency", userInfo.customer_currency.toString())
         Log.i("url",kioskVolleyService.url!!)
         kioskVolleyService.CreateStringRequest(params)
     }

     fun CaptureIntent(id:String){
         val kioskVolleyService=KioskVolleyService()
         progressDialog=Util.showProgressDialog(this,"Capturing intent....")
         kioskVolleyService.url=Apis.BASE_URL+"phpexpert_payment_Capture_Payment_Terminal.php"
         kioskVolleyService.context=this
         kioskVolleyService.kioskResult=this
         kioskVolleyService.type="capture_Intent"
         val userInfo=DroidPrefs.get(this,"userinfo", UserInfo::class.java)
         val params=HashMap<String,String>()
         params.put("api_key", userInfo.api_key.toString())
         params.put("lang_code", userInfo.customer_default_langauge.toString())
         params.put("PAYMENT_INTENT_ID",id)
         kioskVolleyService.CreateStringRequest(params)

     }

     override fun onUnexpectedReaderDisconnect(reader: Reader) {

     }


 }