package com.phpexperts.kioskapp.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phpexperts.kioskapp.Adapters.CartAdapter
import com.phpexperts.kioskapp.Models.ItemExtraGroup
import com.phpexperts.kioskapp.Models.SubExtraItemsRecord
import com.phpexperts.kioskapp.Models.ToppingItems
import com.phpexperts.kioskapp.Models.UserInfo
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.Apis
import com.phpexperts.kioskapp.Utils.DroidPrefs
import com.phpexperts.kioskapp.Utils.ExtraClick
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import kotlinx.android.synthetic.main.layout_cart.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ComExtraActivity:AppCompatActivity(),KioskVolleyService.KioskResult, ExtraClick {
    var item_id = 0
    var size_id = 0
    var name = ""
    var desc = ""
    var price = ""
    var top_allowed = ""
    var com_slot = 0
    private var extra_item_list_id: ArrayList<Int>? = null
    private var temp_extra_item_list_id: ArrayList<Int>? = null
    private var extra_item_list_name: ArrayList<String>? = null
    private var extra_item_list_price: ArrayList<String>? = null
    var free_allowed = 0
    var free_selection_allowed = 0
    var cartAdapter:CartAdapter?=null
    var topp_price = ""
    var itemExtraGroup=ArrayList<ItemExtraGroup>()
    var   toppingItems=ArrayList<ToppingItems>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cart)
        relative_count.visibility= View.GONE
        item_id = intent.getIntExtra("item_id", 0)
        size_id = intent.getIntExtra("size_id", 0)
        name = intent.getStringExtra("name")!!
        desc = intent.getStringExtra("desc")!!
        price = intent.getStringExtra("price")!!
        com_slot = intent.getIntExtra("com_slot", 0)
        free_allowed = intent.getIntExtra("free_allowed", 0)
        free_selection_allowed = intent.getIntExtra("free_selection_allowed", 0)
        txt_dish_name.text=name
        txt_order_amount.text=getString(R.string.pound_symbol)+price
        txt_description.text=desc
        extra_item_list_id = ArrayList()
        temp_extra_item_list_id = ArrayList()
        extra_item_list_name = ArrayList()
        extra_item_list_price = ArrayList()
        top_allowed = intent.getStringExtra("top_allowed")!!
        img_back.setOnClickListener { finish() }
        if (free_allowed > 0) {
            txt_free_toppings.visibility = View.VISIBLE
            var free: String = getString(R.string.free_topp)
            free = free.replace("$", free_allowed.toString())
            txt_free_toppings.text = free
        } else {
            txt_free_toppings.visibility = View.GONE
        }
        getItemExtraData()
        txt_continue.setOnClickListener {
            if(cartAdapter!!.selected_items.size>0){
                for(cart in cartAdapter!!.selected_items){
                    val topping=ToppingItems()
                    topping.item_id=item_id.toString()
                    topping.topping_price=cart.Food_Price_Addons
                    topping.topping_name=cart.Food_Addons_Name
                    topping.topping_id=cart.ExtraID
                    topping.item_name=name
                    toppingItems.add(topping)
                }
            }
            if(toppingItems.size>0){
                var top_price = 0.0
                var stringBuilder = StringBuilder()
                var id = StringBuilder()
                for(i in 0 until toppingItems.size){
                    val topping=toppingItems.get(i)
                    if(free_allowed>0){
if(i>=free_allowed){
    top_price=top_price+topping.topping_price!!.toDouble()
}
                    }
                    else{
                        top_price=top_price+topping.topping_price!!.toDouble()
                    }

                    stringBuilder.append(topping.topping_name)
                    stringBuilder.append("_")
                    id.append(topping.topping_id)
                    id.append("_")

                }
                val intent = Intent()
                topp_price=top_price.toString()
                topp_price = String.format("%.2f", topp_price.toDouble())
                if (stringBuilder.length > 0) {
                    stringBuilder = stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("_"))
                    id = id.deleteCharAt(id.lastIndexOf("_"))
                }
                intent.putExtra("item", stringBuilder.toString())
                intent.putExtra("ids", id.toString())
                intent.putExtra("price", topp_price)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }


    }
    fun getItemExtraData(){
        progress_toppings.visibility=View.VISIBLE
        val volleyService= KioskVolleyService()
        volleyService.type="com_extra_details"
        volleyService.context=this
        volleyService.kioskResult=this
        volleyService.url= Apis.BASE_URL+"phpexpert_food_combo_items_extra.php"
        Log.i("url",volleyService.url!!)
        val userInfo= DroidPrefs.get(this,"userinfo", UserInfo::class.java)
        val params=HashMap<String,String>()
        params.put("ItemID", item_id.toString())
        params.put("lang_code", userInfo.customer_default_langauge!!)
        params.put("api_key", userInfo.api_key!!)
        params.put("FoodItemSizeID", size_id.toString())
        params.put("comboslot_id", com_slot.toString())
        volleyService.CreateStringRequest(params)
    }

    override fun onResult(response: JSONObject, type: String) {
        if(type.equals("com_extra_details")){
            progress_toppings.visibility=View.GONE
            if (response.has("Menu_ItemExtraGroup")) {
                val jsonArray: JSONArray = response.getJSONArray("Menu_ItemExtraGroup")
                if (jsonArray.length() > 0) {
                    val Menu_ItemExtraGroup = jsonArray.getJSONArray(0)
                    val gson = Gson()
                    val listType = object : TypeToken<List<ItemExtraGroup?>?>() {}.type
                    itemExtraGroup=gson.fromJson(Menu_ItemExtraGroup.toString(), listType)
                    if(itemExtraGroup.size>0){
setAdapters()
                    }
                }
            }
        }
    }
    fun setAdapters(){
        txt_no_extra.visibility=View.GONE
        if(itemExtraGroup.get(0).Food_addons_selection_Type.equals("Checkbox")){
            txt_top_head.visibility=View.VISIBLE
            txt_top_head.text=itemExtraGroup.get(0).Food_Group_Name
        }
        else{
            txt_top_head.visibility=View.GONE
        }
        val linearLayoutManager= LinearLayoutManager(this)
      cartAdapter= CartAdapter(itemExtraGroup.get(0).subExtraItemsRecord,itemExtraGroup,this,this)
        recyler_extras.adapter=cartAdapter
        recyler_extras.layoutManager=linearLayoutManager
    }

    override fun ExtraClicked(food: String, food_price: String, food_id: Int, extraitems: ArrayList<SubExtraItemsRecord>) {
        val toppingItem= ToppingItems()
        toppingItem.item_id=item_id.toString()
        toppingItem.topping_price=food_price
        toppingItem.topping_name=food
        toppingItem.topping_id=food_id
        toppingItem.item_name=name

        if(toppingItems.contains(toppingItem)){
            toppingItems.remove(toppingItem)
            toppingItems.add(toppingItem)
        }
        else{
            var added = -1
            for (i in 0 until extraitems.size) {
                for (j in 0 until toppingItems.size) {
                    if (extraitems.get(i).ExtraID==toppingItems.get(j).topping_id) {
                        added = j
                        break
                    }
                }
            }
            if(added>=0){
                toppingItems.removeAt(added)
            }
            toppingItems.add(toppingItem)
        }
    }
}