package com.example.android.cloudinarysample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.downloader.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val SELECT_VIDEO: Int = 100
    private val STORAGE_PERMISSION: Int = 200
    lateinit var TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.TAG = localClassName
        /*button_upload_video.setOnClickListener {
            if (checkStoragePermission()) {
                openMediaChooser()
            } else {
                requestPermission()
            }
        }*/
        Log.d(TAG,getRootDirPath())
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun openMediaChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK) {
            progress_bar.visibility = VISIBLE
            MediaManager.get()
                    .upload(data!!.data)
                    .unsigned("YOUR_PRESET")
                    .option("resource_type", "video")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            Log.d(TAG, "upload onStart")
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            Log.d(TAG, bytes.toString() + "-" + totalBytes.toString())
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            val publicId:String = resultData["public_id"] as String
                            Toast.makeText(this@MainActivity, "Upload successful", Toast.LENGTH_LONG).show()
                            Log.d(TAG, resultData.toString())
                            val gifUrl: String = MediaManager.get()
                                    .url()
                                    .resourceType("video")
                                    .generate("$publicId.gif")
                            downloadGIF(gifUrl,"$publicId.gif")
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            Log.d(TAG, error.description)
                            progress_bar.visibility = INVISIBLE
                            Toast.makeText(this@MainActivity, "Upload was not successful", Toast.LENGTH_LONG).show()
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            Log.d(TAG, "onReschedule")
                        }
                    }).dispatch()

        }

    }

    private fun downloadGIF(url: String, name: String) {
        val directory = getRootDirPath()

        val downloadId =
                PRDownloader
                        .download(url, directory, name).build()
                        .setOnStartOrResumeListener(object : OnStartOrResumeListener {
                            override fun onStartOrResume() {
                                Log.d(TAG,"download started")
                            }
                        })
                        .setOnPauseListener(object : OnPauseListener {
                            override fun onPause() {

                            }
                        })
                        .setOnCancelListener(object : OnCancelListener {
                            override fun onCancel() {

                            }
                        })
                        .setOnProgressListener(object : OnProgressListener {
                            override fun onProgress(progress: Progress) {

                            }
                        })
                        .start(object : OnDownloadListener {
                            override fun onDownloadComplete() {
                                progress_bar.visibility = INVISIBLE
                                Toast.makeText(this@MainActivity,"Download complete",Toast.LENGTH_LONG).show()
                            }

                            override fun onError(error: Error) {
                                Log.d(TAG,error.toString())
                            }
                        })

    }

    private fun getRootDirPath(): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file = ContextCompat.getExternalFilesDirs(this@MainActivity,
                    null)[0]
            file.absolutePath
        } else {
            this@MainActivity.filesDir.absolutePath
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openMediaChooser()
        }
    }

}
