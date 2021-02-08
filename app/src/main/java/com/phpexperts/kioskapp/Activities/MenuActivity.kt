package com.phpexperts.kioskapp.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phpexperts.kioskapp.Adapters.BannerAdapter
import com.phpexperts.kioskapp.Adapters.MenuAdapter
import com.phpexperts.kioskapp.Adapters.SideMenuAdapter
import com.phpexperts.kioskapp.Models.*
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.*
import kotlinx.android.synthetic.main.layout_menu_screen.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class  MenuActivity :AppCompatActivity(), KioskVolleyService.KioskResult {
    var name = ""
    var phone = ""
    var type = ""
    val side_menu_list = ArrayList<SideMenuItem>()
    val menu_list = ArrayList<MenuItem>()
    var banner_list = ArrayList<BannerItem>()
    var menu_cat_list = ArrayList<MenuCat>()
    var subItemRecords = ArrayList<SubItemRecords>()
    var currentPage = 0
    val DELAY_MS: Long = 500
    val PERIOD_MS: Long = 3000
    var item_id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_menu_screen)
        val guestUser = DroidPrefs.get(this, "guest", GuestUser::class.java)
        val user = DroidPrefs.get(this, "user", User::class.java)
        if (!guestUser.phone.equals("")) {
            img_logout.visibility = View.GONE
            txt_user_name.text = guestUser.name
            txt_user_phone.text = guestUser.phone
        } else {
            if (user.CustomerId != null) {
                img_logout.visibility = View.VISIBLE
                txt_user_name.text = user.user_name
                txt_user_phone.text = user.user_phone
            }
        }


        type = intent.getStringExtra("type").toString()
        txt_done.setOnClickListener {
            startActivity(Intent(this, PaymentSelectionActivity::class.java))
        }
        txt_card_payment.setOnClickListener {
//            startActivity(Intent(this, SelectCardType::class.java))
        }
        img_cart.setOnClickListener {
//            startActivity(Intent(this,ActivityCart::class.java).putExtra("dish_name", "Paper Dosa "))
            startActivity(Intent(this, CancelOrderActivity::class.java).putExtra("type", type))
        }
        getAllCategories()
        getBanners()
        getRestroInfo()
        requestPermissions()
        img_logout.setOnClickListener {
            Logout()
        }


//        setAdapters()
    }

    fun Logout() {
        val builder = AlertDialog.Builder(this).setMessage(getString(R.string.logout_text)).setPositiveButton(getString(R.string.ok)) { dialog, which ->
            DroidPrefs.getDefaultInstance(this).clearkey("user")
            val cartDatabase = CartDatabase.getDataBase(this)
            val orderCartDao = cartDatabase!!.OrderCartDao()
            val toppingDao = cartDatabase.ToppingDao()
            orderCartDao!!.DeleteAll()
            toppingDao!!.DeleteAll()
            dialog.dismiss()
            startActivity(Intent(this, CustomerInfoActivity::class.java))
            finish()
        }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }.create().show()
    }

    fun AutoRotateBanners() {
        val Handler = Handler()
        val update = Runnable {
            if (currentPage == banner_list.size - 1) {
                currentPage = 0
            }
            banner_slider.setCurrentItem(currentPage++, true)
        }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                Handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    fun getBanners() {
        val kioskVolleyService = KioskVolleyService()
        val userInfo = DroidPrefs.get(this, "userinfo", UserInfo::class.java)
        val params = HashMap<String, String>()
        params.put("lang_code", userInfo.customer_default_langauge!!)
        params.put("api_key", userInfo.api_key!!)
        kioskVolleyService.url = Apis.BASE_URL + "phpexpert_home_slider.php"
        kioskVolleyService.context = this
        kioskVolleyService.type = "banners"
        kioskVolleyService.kioskResult = this
        kioskVolleyService.CreateStringRequest(params)
    }

    fun setAdapters() {
        val bannerAdapter = BannerAdapter(banner_list, this)
        banner_slider.adapter = bannerAdapter
        AutoRotateBanners()
    }

    fun getAllCategories() {
        progress_menu.visibility = View.VISIBLE
        val kioskVolleyService = KioskVolleyService()
        val userInfo = DroidPrefs.get(this, "userinfo", UserInfo::class.java)
        val params = HashMap<String, String>()
        params.put("resid", userInfo.resid!!)
        params.put("lang_code", userInfo.customer_default_langauge!!)
        params.put("api_key", userInfo.api_key!!)
        kioskVolleyService.url = Apis.BASE_URL + "phpexpert_all_category_list.php"
        kioskVolleyService.context = this
        kioskVolleyService.type = "all_categories"
        kioskVolleyService.kioskResult = this
        kioskVolleyService.CreateStringRequest(params)
    }

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permission, 1000)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        }
    }

    override fun onResult(response: JSONObject, type: String) {
        if (type.equals("all_categories")) {
            progress_menu.visibility = View.GONE
            Log.i("response", response.toString())
            if (response.has("Menu_Cat")) {
                if (menu_cat_list.size > 0) {
                    menu_cat_list.clear()
                }
                val menu_json = response.getJSONArray("Menu_Cat")
                val gson = Gson()
                val type = object : TypeToken<ArrayList<MenuCat>>() {}.type
                menu_cat_list = gson.fromJson<ArrayList<MenuCat>>(menu_json.toString(), type)
                if (menu_cat_list.size > 0) {
                    setMenuAdapter()
                    subItemRecords = menu_cat_list.get(0).subItemsRecord!!

                    txt_menu.text = menu_cat_list.get(0).category_name
                    setSubItemMenu()
                }
            }
        } else if (type.equals("banners", true)) {
            Log.i("response", response.toString())
            if (response.has("FrontBannersList")) {
                val banner_array = response.getJSONArray("FrontBannersList")
                val gson = Gson()
                val type = object : TypeToken<ArrayList<BannerItem>>() {}.type
                banner_list = gson.fromJson<ArrayList<BannerItem>>(banner_array.toString(), type)
                if (banner_list.size > 0) {
                    setAdapters()
                }
            }
        } else if (type.equals("res_info", true)) {
            Log.i("response", response.toString())
            if (response.has("restaurant_Logo")) {
                val restaurant_Logo = response.getString("restaurant_Logo")
                if (restaurant_Logo != null) {
                    Glide.with(this).load(restaurant_Logo).into(img_logo)
                }
            }
        }
    }

    fun setMenuAdapter() {
        val linearLayoutManager = LinearLayoutManager(this)
        val menuAdapter = SideMenuAdapter(menu_cat_list, object : SideMenuAdapter.MenuClicked {
            override fun Clicked(view: View, position: Int) {

                txt_menu.text = menu_cat_list.get(position).category_name
                subItemRecords = menu_cat_list.get(position).subItemsRecord!!
                if (menu_cat_list.get(position).Combo_Available.equals("yes", true)) {
                    if (menu_cat_list.get(position).ComboList != null) {
                        for (i in 0 until menu_cat_list.get(position).ComboList!!.size) {
                            val comboList: ComboLists = menu_cat_list.get(position).ComboList!!.get(i)
                            val subItemsRecord = SubItemRecords()
                            subItemsRecord.RestaurantPizzaItemName = comboList.deal_name
                            subItemsRecord.RestaurantPizzaItemOldPrice = comboList.RestaurantPizzaItemOldPrice
                            subItemsRecord.RestaurantPizzaItemPrice = comboList.RestaurantPizzaItemPrice
                            subItemsRecord.ResPizzaDescription = comboList.deal_description
                            subItemsRecord.extraavailable = "no"
                            subItemsRecord.sizeavailable = "no"
                            subItemsRecord.RestaurantPizzaID = comboList.dealID
                            subItemsRecord.Food_NameNo = comboList.Combo_NameNo
                            subItemsRecord.is_com = true
                            subItemRecords.add(subItemsRecord)

                        }
                    }
                }
                if (subItemRecords.size > 0) {
                    setSubItemMenu()
                }
            }
        })
        recyler_side_menu.adapter = menuAdapter
        recyler_side_menu.layoutManager = linearLayoutManager
        recyler_side_menu.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val pos = linearLayoutManager.findFirstVisibleItemPosition()
            menuAdapter.selected_position = pos
            menuAdapter.notifyDataSetChanged()
            txt_menu.text = menu_cat_list.get(pos).category_name
            subItemRecords = menu_cat_list.get(pos).subItemsRecord!!
            if (subItemRecords.size > 0) {
                setSubItemMenu()
            }
            Log.i("response", pos.toString())
        }
    }

    fun setSubItemMenu() {
        val tabletSize = resources.getBoolean(R.bool.isTablet)

        if (tabletSize) {
            // do something
            val menu_manager = GridLayoutManager(this, 2)
            recyler_menu.layoutManager = menu_manager
        } else {
            val menu_manager = LinearLayoutManager(this)
            recyler_menu.layoutManager = menu_manager
            // do something else
        }
//        val menu_manager=GridLayoutManager(this,2)
        val adapter = MenuAdapter(subItemRecords, this, object : MenuAdapter.AddClciked {
            override fun Clicked(view: View, position: Int) {
                val subItemRecords = subItemRecords.get(position)
                if (subItemRecords.sizeavailable.equals("yes", true) || subItemRecords.extraavailable.equals("yes", true)) {
                    val intent = Intent(this@MenuActivity, ActivityCart::class.java)
                    intent.putExtra("sub_item", subItemRecords)
                    intent.putExtra("type", type)
                    startActivity(intent)
                } else if (!subItemRecords.is_com) {
                    AddtoDatabase(subItemRecords)
                } else {
                    startActivity(Intent(this@MenuActivity, ChooseComActivity::class.java).putExtra("com_id", subItemRecords.RestaurantPizzaID).putExtra("combo", subItemRecords.RestaurantPizzaItemName).putExtra("desc", subItemRecords.ResPizzaDescription).putExtra("price", subItemRecords.RestaurantPizzaItemPrice))
                }
            }
        })
        recyler_menu.adapter = adapter

    }

    fun getRestroInfo() {
        progress_menu.visibility = View.VISIBLE
        val kioskVolleyService = KioskVolleyService()
        val userInfo = DroidPrefs.get(this, "userinfo", UserInfo::class.java)
        val params = HashMap<String, String>()
        params.put("resid", userInfo.resid!!)
        params.put("lang_code", userInfo.customer_default_langauge!!)
        params.put("api_key", userInfo.api_key!!)
        kioskVolleyService.url = Apis.BASE_URL + "phpexpert_restaurant_information.php"
        kioskVolleyService.context = this
        kioskVolleyService.type = "res_info"
        kioskVolleyService.kioskResult = this
        kioskVolleyService.CreateStringRequest(params)
    }

    override fun onResume() {
        super.onResume()
        UpdateCart()
    }

    fun UpdateCart() {
        val cartDatabse = CartDatabase.getDataBase(this)
        val cartDao = cartDatabse!!.OrderCartDao()
        var cartitems = cartDao!!.getCartItems()
        var cart_size = 0
        cart_size = cart_size + cartitems.size
        val com_list = DroidPrefs.get(this, "com_list", String::class.java)
        if (com_list != null) {
            val gson = Gson()
            val listType = object : TypeToken<List<ComItemList?>?>() {}.type
            val comItemLists = gson.fromJson<List<ComItemList>>(com_list, listType)
            if(comItemLists!=null) {
                if (comItemLists.size > 0) {
                    cart_size = cart_size + comItemLists.size

                }
            }
            if (cart_size >0) {
                txt_cart_number.visibility = View.VISIBLE
                txt_cart_number.setText("" + cart_size)
            } else {
                txt_cart_number.visibility = View.GONE

            }
        }
    }


        fun AddtoDatabase(subItemRecords: SubItemRecords) {
            val cartDatabse = CartDatabase.getDataBase(this)
            val cartDao = cartDatabse!!.OrderCartDao()
            val toppingDao = cartDatabse!!.ToppingDao()
            val cart_item = cartDao!!.getOrderItem(subItemRecords.RestaurantPizzaItemName!!)
            if (cart_item != null) {
                val toppingItems = toppingDao!!.getToppingsbyItem(cart_item.item_name!!)
                if (toppingItems.size > 0) {

                } else {
                    var quantity = cart_item.quantity
                    quantity = quantity + 1
                    cart_item.quantity = quantity
                    cartDao.Update(cart_item)
                }
            } else {
                val orderCartItem = OrderCartItem()
                orderCartItem.item_name = subItemRecords!!.RestaurantPizzaItemName
                orderCartItem.item_image = subItemRecords!!.food_Icon
                orderCartItem.item_size_type = subItemRecords!!.RestaurantPizzaItemName


                orderCartItem.item_size_id = "0"

                orderCartItem.item_price = subItemRecords.RestaurantPizzaItemPrice
                orderCartItem.quantity = 1
                orderCartItem.total_price = subItemRecords.RestaurantPizzaItemPrice
                orderCartItem.com=false
                orderCartItem.item_id=subItemRecords.ItemID
                orderCartItem.deal_id=subItemRecords.ItemID
                cartDao.Insert(orderCartItem)
            }
            Toast.makeText(this, subItemRecords.RestaurantPizzaItemName + " " + "Added to Cart!!", Toast.LENGTH_SHORT).show()
            UpdateCart()


        }

    override fun onBackPressed() {
        KioskApplication.finish_activity=true
        finish()
    }


    }
