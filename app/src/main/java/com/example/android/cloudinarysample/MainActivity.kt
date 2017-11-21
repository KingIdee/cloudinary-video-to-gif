package com.example.android.cloudinarysample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import java.io.File


class MainActivity : AppCompatActivity() {

    private val SELECT_VIDEO: Int = 100
    lateinit var TAG:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.TAG = localClassName
        openMediaChooser()
    }

    fun openMediaChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK) {
            MediaManager.get()
                    .upload(data!!.data)
                    .unsigned("YOUR_PRESET")
                    .option("resource_type", "video")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            Log.d("TAG", "onStart")
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            Log.d("TAG", bytes.toString() + "/" + totalBytes.toString())
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            Toast.makeText(this@MainActivity, "success", Toast.LENGTH_LONG).show()
                            Log.d("TAG", "onSuccess")
                            Log.d("TAG", resultData.toString())
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            Log.d("TAG", "onError")
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            Log.d("TAG", "onReschedule")
                        }
                    }).dispatch()

        }

    }

}
