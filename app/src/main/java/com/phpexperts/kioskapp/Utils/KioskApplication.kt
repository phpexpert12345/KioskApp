package com.phpexperts.kioskapp.Utils

import android.app.Application
import com.stripe.stripeterminal.TerminalLifecycleObserver

class KioskApplication:Application() {
    private val observer :TerminalLifecycleObserver=TerminalLifecycleObserver.getInstance()
    companion object{
        var finish_activity:Boolean=false
    }
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(observer)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        observer.onTrimMemory(level,this)
    }
}