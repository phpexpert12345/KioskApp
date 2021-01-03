package com.phpexperts.kioskapp.Models

import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley


class MySingleton private constructor(context: Context) {
    private var mRequestQueue: RequestQueue?
    val imageLoader: ImageLoader

    // getApplicationContext() is key, it keeps you from leaking the
    // Activity or BroadcastReceiver if someone passes one in.
    val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext())
            }
            return mRequestQueue
        }

    fun <T> addToRequestQueue(req: Request<Any>?) {
        requestQueue!!.add<Any>(req)
    }

    companion object {
        private var mInstance: MySingleton? = null
        private lateinit var mCtx: Context

        @Synchronized
        fun getInstance(context: Context): MySingleton? {
            if (mInstance == null) {
                mInstance = MySingleton(context)
            }
            return mInstance
        }
    }

    init {
        mCtx = context
        mRequestQueue = requestQueue
        imageLoader = ImageLoader(mRequestQueue,
                object : ImageLoader.ImageCache {
                    private val cache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(20)
                    override fun getBitmap(url: String): Bitmap {
                        return cache.get(url)!!
                    }

                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                })
    }
}
