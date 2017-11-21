package com.example.android.cloudinarysample

import android.app.Application
import com.cloudinary.android.MediaManager

/**
 * Created by idorenyin on 11/20/17.
 */

public class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this)
    }
}