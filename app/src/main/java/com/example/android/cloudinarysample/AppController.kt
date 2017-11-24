package com.example.android.cloudinarysample

import android.app.Application
import com.cloudinary.android.MediaManager
import com.downloader.PRDownloader

/**
 * Created by idorenyin on 11/20/17.
 */

public class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this)
        PRDownloader.initialize(this)
    }
}