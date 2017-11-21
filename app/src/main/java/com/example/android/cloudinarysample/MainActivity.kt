package com.example.android.cloudinarysample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cloudinary.Cloudinary
import com.cloudinary.Configuration
import com.cloudinary.utils.ObjectUtils
import android.provider.MediaStore
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.provider.OpenableColumns
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private val SELECT_VIDEO:Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openMediaChooser()

    }

    fun openMediaChooser(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode==SELECT_VIDEO && resultCode==Activity.RESULT_OK){
            val config = HashMap<String,Any?>()
            config.put("cloud_name","CLOUDINARY_NAME")
            val cloudinary = Cloudinary(config)

            val myFile = File(data!!.data.toString())
            Log.d("TAG", myFile.toString())
            Log.d("TAG", getRealPathFromURI(this@MainActivity,data.data))
            Executors.newFixedThreadPool(3).execute({
                cloudinary.uploader().unsignedUpload(getRealPathFromURI(this@MainActivity,data.data), "ddsfdcvz",ObjectUtils.asMap("resource_type", "video"))
            })
        }

    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }





}
