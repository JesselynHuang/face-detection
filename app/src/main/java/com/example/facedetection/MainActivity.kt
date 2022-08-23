package com.example.facedetection

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facedetection.databinding.ActivityMainBinding
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import android.widget.ToggleButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.android.gms.common.annotation.KeepName
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.interfaces.R
//import com.google.mlkit.vision.face
//import com.example.facedetection.CameraSourcePreview
import java.io.IOException
import java.util.ArrayList
import androidx.camera.view.PreviewView

import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.camera.CameraXSource
import java.security.cert.CertSelector


val REQUEST_IMAGE_CAPTURE = 1
val REQUEST_CODE = 200

@KeepName
class MainActivity : AppCompatActivity() {
    // Notes kotlin
    // lateinit var --> to declare variable that will be / initialize use in the future
    // val ---> a variable whose value never changes
    // var ---> a variable whose value can change
    // for mac =  option + enter ---> Quick fix error [import library, etc]

    private lateinit var layout: View
    private lateinit var binding: ActivityMainBinding

    //private var cameraSource: CameraSource? = null
    private var cameraSource: CameraXSource? = null
    private var preview: PreviewView? = null
    private var graphicOverlay =  null
    //private var selectedModel = OBJECT_DETECTION

    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector


    private val requestPermissionCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted: Boolean -> if (isGranted) {
            Log.i("Permission: ", "Granted")
    } else {
            Log.i("Permission: ", "Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
        onClickPermission(view)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        previewCamera()

        //preview = FrameLayout.findViewById(R.id)
        //preview = findViewById(R.id)
//        preview = findViewById(com.example.facedetection.R.id.preview_view)
//        if (preview === null) {
//            Log.d("TAG", "Preview is null")
//        }
//        graphicOverlay = findViewById(com.example.facedetection.R.id.graphic_overlay)
//        if (graphicOverlay === null) {
//            Log.d("TAG", "Graphic Overlay is null")
//        }
//
//        val spinner = findViewById<Spinner>(com.example.facedetection.R.id.spinner)
//        val options: MutableList<String> = ArrayList()
////        options.add(OBJECT_DETECTION)
////        options.add(OBJECT_DETECTION)
//
//        val dataAdapter = ArrayAdapter(this, com.example.facedetection.R.layout.spinner_style, options)
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = dataAdapter
        //spinner.onItemSelectedListener = On

//        val facingSwitch = findViewById<ToggleButton>(com.example.facedetection.R.id.facing_switch)
//        //facingSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener())
//
//        val settingsButton = findViewById<ImageView>(com.example.facedetection.R.id.settings_button)
//        settingsButton.setOnClickListener{
//            val intent = Intent(applicationContext, Settings::class.java)
//            //intent.putExtra()
//        }
        //takePhoto()
    }

//    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            //deprecated
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//        } catch (e: ActivityNotFoundException) {
//            //display error state to the user
//        }
//    }


    private fun previewCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (e: Exception) {
                Log.d("TAG", "Failed....")
            }
        }, ContextCompat.getMainExecutor(this))
    }

//    fun takePhoto() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        resultLauncher.launch(cameraIntent)
//        Log.i("TAG","Halo camera is taking....")
//    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode === Activity.RESULT_OK) {
            val data: Intent? = result.data
            //takePhoto()
            //Log.i("DATA", data)
        }
    }

    fun onClickPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                layout.showSnackbar(
                    view,
                    getString(com.example.facedetection.R.string.permission_granted),
                    Snackbar.LENGTH_INDEFINITE,
                    null
                ) {}
            } // end bracket first conditional
            ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) -> {
                layout.showSnackbar(
                    view,
                    getString(com.example.facedetection.R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(com.example.facedetection.R.string.ok)
                ) {
                    requestPermissionCamera.launch(
                        android.Manifest.permission.CAMERA
                    )
                    previewCamera()
                }
            }
            else -> {
                requestPermissionCamera.launch(
                    android.Manifest.permission.CAMERA
                )
                previewCamera()
            }
        }
    }
}
// fun --> function in kotlin
// char sequence --> variable char
fun View.showSnackbar (
    view: View,
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage !== null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}





