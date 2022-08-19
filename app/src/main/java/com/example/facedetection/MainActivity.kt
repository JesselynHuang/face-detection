package com.example.facedetection
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)
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





