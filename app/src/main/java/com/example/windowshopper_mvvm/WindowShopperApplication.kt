package com.example.windowshopper_mvvm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class WindowShopperApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }

}