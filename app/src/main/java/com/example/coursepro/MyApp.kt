package com.example.coursepro

import android.app.Application
import android.content.Context


/*
This class is used to access the app's context anywhere in the app
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        private var context: Context? = null
        val appContext: Context?
            get() = context
    }
}