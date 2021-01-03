package com.phpexperts.kioskapp.Utils

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.phpexperts.kioskapp.Models.MySingleton
import org.json.JSONObject

class KioskVolleyService {
    var url : String?=null
    var type : String?=null
    var params :JSONObject?=null
    var response : JSONObject?=null
    var context: Context?=null
    var kioskResult :KioskResult?=null
    interface KioskResult{
        fun onResult(response: JSONObject, type:String)
    }


    fun createJsonRequest(){
        val requestQueue= MySingleton.getInstance(context!!)!!.requestQueue
        val jsonObjectRequest =JsonObjectRequest(Request.Method.POST,url,params, Response.Listener<JSONObject> {
            kioskResult!!.onResult(it,type!!)

        }, Response.ErrorListener {

        })
        requestQueue!!.add(jsonObjectRequest)

    }
    fun CreateStringRequest(params:HashMap<String, String>){
        val requestQueue= MySingleton.getInstance(context!!)!!.requestQueue
       val stringRequest=object :StringRequest(Request.Method.POST,url,object :Response.Listener<String>{
           override fun onResponse(response: String?) {
val json=JSONObject(response)
               kioskResult!!.onResult(json,type!!)
           }
       }, object :Response.ErrorListener{
           override fun onErrorResponse(error: VolleyError?) {
Log.i("response", error.toString())
           }
       })
       {
           override fun getParams(): MutableMap<String, String> {

               return params
           }


       }


        requestQueue!!.add(stringRequest)
    }
}