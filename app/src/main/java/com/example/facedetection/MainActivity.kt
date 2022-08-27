package com.example.facedetection

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.otaliastudios.cameraview.controls.Facing

import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    // Notes kotlin
    // lateinit var --> to declare variable that will be / initialize use in the future
    // val ---> a variable whose value never changes
    // var ---> a variable whose value can change
    // for mac =  option + enter ---> Quick fix error [import library, etc]

    private lateinit var layout: View
    //private lateinit var binding: ActivityMainBinding

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
        setContentView(R.layout.activity_main)

        val lensFacing =
            savedInstanceState?.getSerializable(KEY_LENS_FACING) as Facing? ?: Facing.BACK
        onClickPermission(cameraPreview)
        setupCamera(lensFacing)
    }

    override fun onResume() {
        super.onResume()
        //viewfinder.start()
        //viewfinder.onStartTemporaryDetach()
        cameraPreview.open()

    }

    override fun onPause() {
        super.onPause()
        cameraPreview.close()
        //viewfinder.stopVideo()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(KEY_LENS_FACING, cameraPreview.facing)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraPreview.destroy()
    }

    private fun setupCamera(lensFacing: Facing) {
        val faceDetector = Detector(FrameView)
        cameraPreview.facing = lensFacing
        cameraPreview.addFrameProcessor {
            faceDetector.process(
                Model (
                    data = it.getData(),
                    rotation = it.rotationToUser,
                    size = Size(it.size.width, it.size.height),
                    format = it.format,
                    lensFacing = if (cameraPreview.facing == Facing.BACK) LensFacing.BACK else LensFacing.FRONT
                )
            )
        }

        bottom.setOnClickListener {
            cameraPreview.toggleFacing()
        }
    }

    //Preview Camera
//    private fun previewCamera() {
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(binding.previewView.surfaceProvider)
//            }
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
//
//            } catch (e: Exception) {
//                Log.d("TAG", "Failed....")
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }

    //Runtime Permission
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
                    //previewCamera()
                }
            }
            else -> {
                requestPermissionCamera.launch(
                    android.Manifest.permission.CAMERA
                )
                //previewCamera()
            }
        }
    }

    companion object {
        private const val KEY_LENS_FACING = "key-lens-facing"
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





