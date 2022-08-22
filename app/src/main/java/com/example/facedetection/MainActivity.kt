package com.example.facedetection
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facedetection.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

val REQUEST_IMAGE_CAPTURE = 1
class MainActivity : AppCompatActivity() {
    // Notes kotlin
    // lateinit var --> to declare variable that will be / initialize use in the future
    // val ---> a variable whose value never changes
    // var ---> a variable whose value can change
    // for mac =  option + enter ---> Quick fix error [import library, etc]

    private lateinit var layout: View
    private lateinit var binding: ActivityMainBinding

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
    }

    fun onClickPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_granted),
                    Snackbar.LENGTH_INDEFINITE,
                    null
                ) {}
            } // end bracket first conditional
            ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
                    requestPermissionCamera.launch(
                        android.Manifest.permission.CAMERA
                    )
                }
            }
            else -> {
                requestPermissionCamera.launch(
                    android.Manifest.permission.CAMERA
                )
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





