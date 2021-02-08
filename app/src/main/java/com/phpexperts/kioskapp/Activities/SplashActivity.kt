package com.phpexperts.kioskapp.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.phpexperts.kioskapp.Models.User
import com.phpexperts.kioskapp.Models.UserInfo
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.Apis
import com.phpexperts.kioskapp.Utils.CartDatabase
import com.phpexperts.kioskapp.Utils.DroidPrefs
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import kotlinx.android.synthetic.main.layout_spalsh.*
import org.json.JSONObject

class  SplashActivity : AppCompatActivity(), KioskVolleyService.KioskResult {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_spalsh)
        getInfoFromServer()
    }
    fun getInfoFromServer(){
        progress_splash.visibility= View.VISIBLE
        val kioskVolleyService=KioskVolleyService()
        kioskVolleyService.url= Apis.BASE_URL+"phpexpert_first_call_api.php"
        Log.i("url",kioskVolleyService.url!!)
        val jsonObject=JSONObject()
        kioskVolleyService.params=jsonObject
        kioskVolleyService.type="first_call"
        kioskVolleyService.context=this
        kioskVolleyService.kioskResult=this
        kioskVolleyService.createJsonRequest()
    }

    override fun onResult(response: JSONObject, type: String) {
       if(type.equals("first_call")){
           val userInfo=UserInfo()
           userInfo.api_key=response.getString("api_key")
           userInfo.customer_currency=response.getString("customer_currency")
           userInfo.resid=response.getString("resid")
           userInfo.customer_default_langauge=response.getString("customer_default_langauge")
            DroidPrefs.apply(this,"userinfo",userInfo)
           progress_splash.visibility= View.GONE
           val user=DroidPrefs.get(this,"user", User::class.java)
           if(user.CustomerId!=null){
               startActivity(Intent(this@SplashActivity,OrderTypeActivity::class.java).putExtra("name", user.user_name).putExtra("phone", user.user_phone))
           }
           else {
               val cartDatabase=CartDatabase.getDataBase(this);
               val cartDao=cartDatabase!!.OrderCartDao();
               val toppingDao=cartDatabase.ToppingDao()
               cartDao!!.DeleteAll()
               toppingDao!!.DeleteAll()
               DroidPrefs.getDefaultInstance(this).clearkey("com_list")
               DroidPrefs.getDefaultInstance(this).clearkey("guest")
               startActivity(Intent(this@SplashActivity,CustomerInfoActivity::class.java))
           }

            finish()
       }
    }
}