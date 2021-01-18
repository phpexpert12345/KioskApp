 package com.phpexperts.kioskapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phpexperts.kioskapp.Adapters.CartAdapter
import com.phpexperts.kioskapp.Adapters.DifferentSizeAdapter
import com.phpexperts.kioskapp.Models.*
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.*
import kotlinx.android.synthetic.main.layout_cart.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.ArrayList

class ActivityCart :AppCompatActivity(), KioskVolleyService.KioskResult {
    var cartItems = ArrayList<CartItem>()
    var name=""
    var type=""
    var amount =0.0
    var subItemRecords:SubItemRecords?=null
    var price :Double=0.0
    var decimalFormat=DecimalFormat("##.00")
    var resturantItemsSize=ArrayList<RestaurantMenItemsSize>()
    var subExtraItemsRecords=ArrayList<SubExtraItemsRecord>()
    var cartAdapter:CartAdapter?=null
    var size_type:String?=null
    var item_id:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cart)
        KioskApplication.finish_activity=false
        if(intent.hasExtra("item_id")){
            item_id=intent.getStringExtra("item_id");

        }
//        setAdapters()
        linear_amount.setOnClickListener{
            finish()
        }

        txt_back.setOnClickListener {
            finish()
        }
        img_back.setOnClickListener { 
            finish()
        }

        img_less.setOnClickListener {
            var count = txt_count.text.toString().toInt()
            if(count==0){
                txt_count.text="0"
                txt_order_amount.text="$15.00"
            }
            else {
                count-=1
                txt_count.text=count.toString()

                 amount=count*price
                txt_order_amount.text=getString(R.string.pound_symbol)+decimalFormat.format(amount)
            }
        }
        img_more.setOnClickListener {
            var count=txt_count.text.toString().toInt()
            count+=1
            txt_count.text=count.toString()
            amount=count*price

            txt_order_amount.text=getString(R.string.pound_symbol)+decimalFormat.format(amount)
        }
        Init()

        txt_continue.setOnClickListener {
            var total_price =0.0
            val orderCartItem =OrderCartItem()
            orderCartItem.item_name=subItemRecords!!.RestaurantPizzaItemName
            orderCartItem.item_image=subItemRecords!!.food_Icon
            orderCartItem.item_size_type=size_type
            orderCartItem.item_price=price.toString()
            orderCartItem.quantity=txt_count.text.toString().toInt()
            val cartDatabase=CartDatabase.getDataBase(this)
            val cartDao=cartDatabase!!.OrderCartDao()
            cartDao!!.Insert(orderCartItem)
            if(cartAdapter!=null){
                var toppings_sum =0.0
                val selected_toppings=cartAdapter!!.selected_items
                Log.i("response", toppings_sum.toString())
                if(selected_toppings.size>0){
                    for(toppings in selected_toppings){
                        val topping_price =toppings.Food_Price_Addons!!.toDouble()
                        toppings_sum+=topping_price
                        val toppingItems =ToppingItems()
                        toppingItems.topping_name=toppings.Food_Addons_Name
                        toppingItems.topping_price=toppings.Food_Price_Addons
                        toppingItems.item_name=subItemRecords!!.RestaurantPizzaItemName
                        toppingItems.item_id=item_id
                        val toppingDao=cartDatabase.ToppingDao()
                        toppingDao!!.Insert(toppingItems)
                        total_price=price+toppings_sum
                    }
                }
            }
            if(total_price>0.0){
                val cartitem=cartDao.getOrderItem(orderCartItem.item_name.toString())
                cartitem.total_price=total_price.toString()
                cartDao.Update(cartitem)
            }

Toast.makeText(this,getString(R.string.cart_added),Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,CancelOrderActivity::class.java).putExtra("type",type))

        }




    }

    override fun onResume() {
        super.onResume()
        if(KioskApplication.finish_activity){
            finish()
        }
    }
    fun Init(){
        subItemRecords= intent.getSerializableExtra("sub_item") as SubItemRecords?
        type=intent.getStringExtra("type")!!
        if(subItemRecords!=null){
            txt_dish_name.text=subItemRecords!!.RestaurantPizzaItemName
            Glide.with(this).load(subItemRecords!!.food_Icon).placeholder(R.drawable.ic_palceholder).into(img_dish)
            txt_description.text=subItemRecords!!.ResPizzaDescription
            txt_order_amount.text=getString(R.string.pound_symbol)+subItemRecords!!.RestaurantPizzaItemPrice
            price =subItemRecords!!.RestaurantPizzaItemPrice!!.toDouble()
            getDifferentSizes()
        }
    }

    fun setAdapters(){
        txt_no_extra.visibility=View.GONE
          val linearLayoutManager=LinearLayoutManager(this)
        cartAdapter=CartAdapter(subExtraItemsRecords,this)
        recyler_extras.adapter=cartAdapter
        recyler_extras.layoutManager=linearLayoutManager
    }
    fun getExtraItems(FoodItemSizeID:String){
        val kioskVolleyService=KioskVolleyService()
        kioskVolleyService.url= Apis.BASE_URL+"phpexpert_food_items_extra.php"
        val userInfo=DroidPrefs.get(this,"userinfo", UserInfo::class.java)
       val params=HashMap<String, String>()
        params.put("api_key", userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("ItemID", subItemRecords!!.ItemID.toString())
        params.put("FoodItemSizeID",FoodItemSizeID)
        Log.i("response", params.toString())
        kioskVolleyService.type="extra_items"
        kioskVolleyService.context=this
        kioskVolleyService.kioskResult=this
        kioskVolleyService.CreateStringRequest(params)
    }
    override fun onResult(response: JSONObject, type: String) {
if(type.equals("extra_items", true)){
     Log.i("response", response.toString())
    if(response.has("Menu_ItemExtraGroup")){
        var json =response.getJSONArray("Menu_ItemExtraGroup")
        if(json.length()>0){
            var array=json.getJSONArray(0)

            if(array !=null && array.length()>0){
                var subextrajson =array.getJSONObject(0)
                if(subextrajson!=null){
                    Log.i("response", subextrajson.toString())

                    var subextraItems=subextrajson.getJSONArray("subExtraItemsRecord")
                    if(subextraItems!=null && subextraItems.length()>0){
                        if(subExtraItemsRecords.size>0){
                            subExtraItemsRecords.clear()
                        }
                        Log.i("response", subextraItems.toString())
                        val gson=Gson()
                    val type=object: TypeToken<ArrayList<SubExtraItemsRecord>>(){}.type
                    subExtraItemsRecords=gson.fromJson<ArrayList<SubExtraItemsRecord>>(subextraItems.toString(),type)
                        if(subExtraItemsRecords!=null && subExtraItemsRecords.size>0){

                            setAdapters()
                        }

                    }

//
                }


            }
        }

    }
}
        else if(type.equals("item_sizes", true)){
    Log.i("response", response.toString())
    if(response.has("RestaurantMenItemsSize")){
        val array=response.getJSONArray("RestaurantMenItemsSize")
        if(array.length()>0){
            if(resturantItemsSize.size>0){
                resturantItemsSize.clear()
            }
            val gson= Gson()

            val type=object: TypeToken<ArrayList<RestaurantMenItemsSize>>(){}.type

            resturantItemsSize=gson.fromJson<ArrayList<RestaurantMenItemsSize>>(array.toString(),type)
            if(resturantItemsSize.size>0) {
                if(resturantItemsSize.get(0).RestaurantPizzaItemName!=null) {
                    setDifferentSizesAdapter()
                }
                else {
                    txt_no_extra.visibility=View.VISIBLE
                }
            }
        }
    }
        }
    }
    fun  getDifferentSizes(){
        val kioskVolleyService=KioskVolleyService()
        kioskVolleyService.url= Apis.BASE_URL+"phpexpert_restaurantMenuItemSize.php"
        val userInfo=DroidPrefs.get(this,"userinfo", UserInfo::class.java)
        val params=HashMap<String, String>()
        params.put("api_key", userInfo.api_key.toString())
        params.put("lang_code", userInfo.customer_default_langauge.toString())
        params.put("ItemID", subItemRecords!!.ItemID.toString())
       kioskVolleyService.type="item_sizes"
        kioskVolleyService.context=this
        kioskVolleyService.kioskResult=this
        kioskVolleyService.CreateStringRequest(params)
    }
    fun setDifferentSizesAdapter(){
        val horizontalmanager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        getExtraItems(resturantItemsSize.get(0 ).FoodItemSizeID.toString())
        val adapter=DifferentSizeAdapter(resturantItemsSize, object:DifferentSizeAdapter.SizeClicked{
            override fun Clicked(view: View, pos: Int) {
                val restro=resturantItemsSize.get(pos)
                price =resturantItemsSize.get(pos).RestaurantPizzaItemPrice!!.toDouble()
                txt_order_amount.text=getString(R.string.pound_symbol)+resturantItemsSize.get(pos).RestaurantPizzaItemPrice
                size_type=restro.RestaurantPizzaItemName!!.substring(0,restro.RestaurantPizzaItemName!!.indexOf("|"))
                getExtraItems(resturantItemsSize.get(pos).FoodItemSizeID.toString())
            }
        },this )
        recyler_sizes.adapter=adapter
        recyler_sizes.layoutManager=horizontalmanager
    }
}