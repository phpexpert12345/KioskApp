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
import com.phpexperts.kioskapp.Adapters.ComValueAdapter
import com.phpexperts.kioskapp.Adapters.ComboSectionAdapter
import com.phpexperts.kioskapp.Models.*
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.DroidPrefs
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import com.phpexperts.kioskapp.Utils.SelectSec
import kotlinx.android.synthetic.main.layout_com_details.*
import org.json.JSONArray
import org.json.JSONObject

class ChooseComActivity:AppCompatActivity(),KioskVolleyService.KioskResult {
    var combo = ""
    var desc = ""
    var price = ""
    var Free_allowed = 0
    var Free_Topping_Selection_allowed = 0
    var comboSections: List<ComboSection> = ArrayList<ComboSection>()
    var com_tops = StringBuilder()
    var sec_item = StringBuilder()
    var sec_value = StringBuilder()
    var com_tops_id = StringBuilder()
    var item_ids = StringBuilder()
    var comboslot_ids = StringBuilder()
    var Combo_Slot_ItemIDs = StringBuilder()
    var FoodItemSizeIDs = StringBuilder()
    var sectio = ""
    var value = ""
    var com_price = ""
    var com_p = 0.0
    var com_id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_com_details)
        com_id = intent.getIntExtra("com_id", 0)
        combo = intent.getStringExtra("combo")!!
        desc = intent.getStringExtra("desc")!!
        price = intent.getStringExtra("price")!!
        txt_com_desc.text = desc
        txt_com_name.text = combo
        btn_add_to_cart.setOnClickListener {
            SaveComboToDatabase()
        }
        getComdetails()
    }
    fun getComdetails(){
        progress_com.visibility = View.VISIBLE
        val volleyService=KioskVolleyService()
        volleyService.type="com_details"
        volleyService.context=this
        volleyService.kioskResult=this
        volleyService.url= "https://www.lieferadeal.de/WebAppAPI/phpexpert_combo_detail.php"
        Log.i("url",volleyService.url!!)
        val userInfo= DroidPrefs.get(this,"userinfo", UserInfo::class.java)
        val params=HashMap<String,String>()
        params.put("combo_id", com_id.toString())
        params.put("lang_code", userInfo.customer_default_langauge!!)
        params.put("api_key", userInfo.api_key!!)
        volleyService.CreateStringRequest(params)

    }

    override fun onResult(response: JSONObject, type: String) {
        progress_com.visibility=View.GONE
if(type.equals("com_details",true)){
    if (response.has("ComboList")) {
        val ComboList: JSONArray = response.getJSONArray("ComboList")
        if (ComboList.length() > 0) {
            val jsonObject1 = ComboList.getJSONObject(0)
            if (jsonObject1.has("ComboSectionList")) {
                val ComboSectionList = jsonObject1.getJSONArray("ComboSectionList")
                val gson = Gson()
                val listType = object : TypeToken<List<ComboSection?>?>() {}.type
                comboSections = gson.fromJson(ComboSectionList.toString(), listType)
            }
        }
        if (comboSections.size > 0) {
            setComboSectionAdapter()
        }
    }
}
    }
    private fun setComboSectionAdapter() {
        val linearLayoutManager = LinearLayoutManager(this)
        val comboSectionAdapter = ComboSectionAdapter(comboSections, object : SelectSec {
            override fun getClickSec(pos: Int) {
                setComboSectionValueAdapter(comboSections[pos].getComboSectionValue() as List<ComValue>)
            }
        })
        recyler_combo.adapter = comboSectionAdapter
        recyler_combo.layoutManager = linearLayoutManager
    }
    private fun setComboSectionValueAdapter(comValues: List<ComValue>) {
        val linearLayoutManager = LinearLayoutManager(this)
        val comValueAdapter = ComValueAdapter(comValues,  object : ComValueAdapter.ComValueClicked {
            override fun ComClicked(subItemsRecord: ComValueItem?, comslot_id: Int, sec_: String?) {
                if (item_ids.length > 0) {
                    item_ids.append(",")
                }
                if (FoodItemSizeIDs.length > 0) {
                    FoodItemSizeIDs.append(",")
                }
                if (Combo_Slot_ItemIDs.length > 0) {
                    Combo_Slot_ItemIDs.append(",")
                }
                item_ids.append(subItemsRecord!!.getItemID())
                if (!subItemsRecord!!.getFoodItemSizeID().equals("")) {
                    FoodItemSizeIDs.append(subItemsRecord.getFoodItemSizeID())
                } else {
                    FoodItemSizeIDs.append("0")
                }
                Combo_Slot_ItemIDs.append(subItemsRecord!!.getCombo_Slot_ItemID())
                val intent = Intent(this@ChooseComActivity, ComExtraActivity::class.java)
                intent.putExtra("price", price)
                intent.putExtra("item_id", subItemsRecord.getItemID())
                if (!subItemsRecord.getFoodItemSizeID().equals("")) {
                    intent.putExtra("size_id", subItemsRecord.getFoodItemSizeID()!!.toInt())
                } else {
                    intent.putExtra("size_id", 0)
                }
                intent.putExtra("name", combo)
                intent.putExtra("desc", desc)
                intent.putExtra("top_allowed", subItemsRecord.getCombo_Topping_Allow())
                intent.putExtra("com_slot", comslot_id)
                intent.putExtra("free_allowed", Free_allowed)
                intent.putExtra("free_selection_allowed", Free_Topping_Selection_allowed)
                sectio = sec_.toString()
                value = subItemsRecord.getCombo_Slot_ItemName()!!
                startActivityForResult(intent, 5)
            }

            override fun comValClicked(comValue: ComValue?) {
                if (comboslot_ids.length > 0) {
                    comboslot_ids.append(",")
                }
                comboslot_ids.append(comValue!!.getComboslot_id())
                if (!comValue!!.getFree_allowed().equals("")) {
                    Free_allowed = comValue.getFree_allowed()!!.toInt()
                    Free_Topping_Selection_allowed = comValue.getFree_Topping_Selection_allowed()!!.toInt()
                }
            }


        },this)
        recyler_sec.adapter = comValueAdapter
        recyler_sec.layoutManager = linearLayoutManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 5) {
                if (data != null) {
                    if (data.hasExtra("price")) {
                        val p = data.getStringExtra("price")
                        val item = data.getStringExtra("item")
                        val ids = data.getStringExtra("ids")
                        if (com_tops.length > 0) {
                            com_tops.append(",")
                        }
                        if (sec_item.length > 0) {
                            sec_item.append(",")
                        }
                        if (sec_value.length > 0) {
                            sec_value.append(",")
                        }
                        if (com_tops_id.length > 0) {
                            com_tops_id.append(",")
                        }
                        val pr = price.toDouble()
                        val price_demo = p!!.toDouble()
                        com_p = if (sec_item.length > 0) {
                            com_p + price_demo
                        } else {
                            com_p + pr + price_demo
                        }
                        com_price = com_p.toString()
                        com_tops.append(item)
                        sec_item.append(sectio)
                        sec_value.append(value)
                        com_tops_id.append(ids)
                    }
                    Log.i("url", "$price, $com_tops, $sec_item, $sec_value, $com_tops_id")
                }
            }
        }
    }
    private fun SaveComboToDatabase() {
        val com_list = DroidPrefs.get(this, "com_list", String::class.java)
        if (com_list != null) {
            val gson = Gson()
            val listType = object : TypeToken<List<ComItemList?>?>() {}.type
            var comItemLists: MutableList<ComItemList?>? = gson.fromJson<MutableList<ComItemList?>>(com_list, listType)
            if (comItemLists != null) {
            } else {
                comItemLists = java.util.ArrayList<ComItemList?>()
            }
            val comItemList = ComItemList()
            comItemList.com_sec = sec_item.toString()
            comItemList.combo_name = combo
            comItemList.sec_value = sec_value.toString()
            comItemList.combo_desc = desc
            comItemList.combo_top = com_tops.toString()
            comItemList.combo_top_id = com_tops_id.toString()
            comItemList.ItemID = item_ids.toString()
            comItemList.Combo_Slot_ItemID = Combo_Slot_ItemIDs.toString()
            comItemList.FoodItemSizeID = FoodItemSizeIDs.toString()
            comItemList.comboslot_id = comboslot_ids.toString()
            comItemList.quantity = 1
            comItemList.deal_id = com_id.toString()
            comItemList.price = com_price
            comItemLists.add(comItemList)
            val com = gson.toJson(comItemLists)
            DroidPrefs.apply(this, "com_list", com)
        }
        finish()
    }
}