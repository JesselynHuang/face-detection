package com.example.facedetection

import android.Manifest
import android.app.Instrumentation
import android.content.ActivityNotFoundException

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat

class FaceDetection : AppCompactActivity {
    // kotlin declare variable use val and var
    // val ---> a variable whose value never changes
    // var ---> a variable whose value can change

//    private lateinit var myImageView: ImageView
//    private val cameraRequestId = 1222
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //setContentView(R.layout.activity_main)
////        myImageView = findViewById(R.id.myImageView)
//        // get permission
//        if (ContextCompat.checkSelfPermission(
//                applicationContext.Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_DENIED
//        )
//            ActivityCompat.requestPermissions()
//    }
    val REQUEST_IMAGE_CAPTURE = 1
    val RESULT_OK = 200
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }



}