package com.phpexperts.kioskapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phpexperts.kioskapp.Models.MenuCat
import com.phpexperts.kioskapp.Models.User
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.Apis
import com.phpexperts.kioskapp.Utils.DroidPrefs
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import kotlinx.android.synthetic.main.customer_info.*
import kotlinx.android.synthetic.main.layout_login.*
import org.json.JSONObject
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity(), KioskVolleyService.KioskResult {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_login)
        btn_login_device.setOnClickListener {
            if(edit_email.text.toString().isEmpty()){
                Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(edit_email.text.toString()).matches()){
                Toast.makeText(this,getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show()
            }
            else if(edit_password.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.enter_password),Toast.LENGTH_SHORT).show()
            }
            else if(edit_password.text.toString().length<3){
                Toast.makeText(this,getString(R.string.enter_valid_password), Toast.LENGTH_SHORT).show()
            }
            else {
                getToken()
            }
        }
        img_back.setOnClickListener {
            finish()
        }
//        getToken()

    }
    fun Login(token:String){
        progress_login.visibility= View.VISIBLE
        val kioskVolleyService=KioskVolleyService()
        val jsonObject=JSONObject()
        val params=HashMap<String,String>()
        params.put("user_email", edit_email.text.toString())
        params.put("device_id", token)
        params.put("device_platform", "Android")
        params.put("user_pass",edit_password.text.toString())
        kioskVolleyService.url= Apis.BASE_URL+"phpexpert_customer_login.php"
        kioskVolleyService.params=jsonObject
        kioskVolleyService.context=this
        kioskVolleyService.kioskResult=this
        kioskVolleyService.type="login"
        kioskVolleyService.CreateStringRequest(params)
    }

    override fun onResult(response: JSONObject, type: String) {
        if(type.equals("login")){
            progress_login.visibility=View.GONE
            if(response.getInt("success")==0){
                val gson =Gson()
                 val type=object: TypeToken<User>(){}.type
                val user=gson.fromJson<User>(response.toString(),type)
                if(user.CustomerId!=null){
                    DroidPrefs.apply(this,"user",user)
                    startActivity(Intent(this, OrderTypeActivity::class.java).putExtra("name", user.user_name).putExtra("phone", user.user_phone))
                    finish()
                }
            }
            else{
                Toast.makeText(this,response.getString("success_msg"),Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Login(token!!)


            // Log and toast

//            Toast.makeText(baseContext, token.toString(), Toast.LENGTH_SHORT).show()
        })
    }
}