package com.aprilianta.cartdummy

import android.app.Application
import com.aprilianta.cartdummy.utils.SunmiPrintHelper

class BaseApp : Application() {


    override fun onCreate() {
        super.onCreate()
        init()
    }

    /**
     * Connect print service through interface library
     */
    private fun init() {
        SunmiPrintHelper.getInstance().initSunmiPrinterService(this)
    }
}