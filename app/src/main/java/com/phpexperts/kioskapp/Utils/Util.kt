package com.phpexperts.kioskapp.Utils

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


class Util {
    companion object{
        fun CheckInternetConnection(context: Context):Boolean{
            var connected = false
            val connectivityManager =context. getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            connected = if (connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                    connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                true
            } else false
            return connected
        }
        fun ConvertToBase64(string: String):String{

            var base64 = ""
            try {
                val byteArray: ByteArray = string.toByteArray()
                base64 = Base64.encodeToString(byteArray,Base64.NO_WRAP)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return base64
        }
        fun showProgressDialog(context: Context, message:String):ProgressDialog{
            val progressDialog=ProgressDialog(context)
            progressDialog.setMessage(message)
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            return progressDialog
        }
    }
}