package com.phpexperts.kioskapp.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat.getSystemService


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
    }
}