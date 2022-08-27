package com.example.facedetection

import android.graphics.RectF
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.GuardedBy
import com.google.android.gms.common.util.concurrent.HandlerExecutor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

import java.lang.Exception
import java.lang.IllegalStateException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Detector (private val FrameView: Frame) {
    private val mlkitFace = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) //used to accuracy or akurat when detect faces
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) // used to identity facial landmarks : eyes, nose, mouth etc
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE) // used to identity category face like eyes close etc
            .setMinFaceSize(MIN_FACE_SIZE) // used to define minimal size of the face
            .enableTracking()
            .build()
    )

    private var onFaceDetectionResult: OnFaceDetectionResult? = null
    private lateinit var faceDetectionExecutor: ExecutorService
    private val mainExecutor = HandlerExecutor(Looper.getMainLooper())
    private val lock = Object()

    //@GuardedBy used to annotated (to make) method or field can only be access when holding the reference lock
    @GuardedBy("lock")
    private var isProcessing = false

    // init in kotlin --> code that will be first to be executed when the class is use
    // syntax :: used to converts kotlin function into a lambda
    init {
        FrameView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View?) {
                faceDetectionExecutor = Executors.newSingleThreadExecutor()
            }

            override fun onViewDetachedFromWindow(view: View?) {
                TODO("Not yet implemented")
                if (::faceDetectionExecutor.isInitialized) {
                    faceDetectionExecutor.shutdown()
                }
            }
        })
    }

    fun setOnFaceDetectionFailure(listener: OnFaceDetectionResult) {
        onFaceDetectionResult = listener
    }

    fun process(frame: Model) {
        synchronized(lock) {
            if (!isProcessing) {
                isProcessing = true
                if (!::faceDetectionExecutor.isInitialized) {
                    val exception = IllegalStateException("Failed to run face detection")
                    onError(exception)
                } else {
                    faceDetectionExecutor.execute { frame.detectFaces() }
                }
            }
        }
    }

    //function detect faces
    private fun Model.detectFaces() {
        val data = data ?: return
        val inputImage = InputImage.fromByteArray(data, size.width, size.height, rotation, format)
        mlkitFace.process(inputImage).addOnSuccessListener {
            faces -> synchronized(lock) {
                isProcessing = false
            }
            val faceBounds = faces.map { face -> face.toFaceBounds(this)}
            mainExecutor.execute { FrameView.updateFaces(faceBounds) }
        }
            .addOnFailureListener{
                exception -> synchronized(lock) {
                    isProcessing = false
            }
                onError(exception)
            }
    }

    private fun Face.toFaceBounds(frame: Model) : FaceBounds {
        val reverseDimens = frame.rotation === 90 || frame.rotation === 270
        val width = if (reverseDimens) frame.size.height else frame.size.width
        val height = if (reverseDimens) frame.size.width else frame.size.height
        val scaleX = FrameView.width.toFloat() / width
        val scaleY = FrameView.height.toFloat() / height
        val isFrontLens = frame.lensFacing == LensFacing.FRONT
        val flippedLeft = if (isFrontLens) width - boundingBox.right else boundingBox.left
        val flippedRight = if (isFrontLens) width - boundingBox.left else boundingBox.right
        val scaledLeft = scaleX * flippedLeft
        val scaledTop = scaleY * boundingBox.top
        val scaledRight = scaleX * flippedRight
        val scaledBottom = scaleY * boundingBox.bottom
        val scaledBoundingBox = RectF(scaledLeft, scaledTop, scaledRight, scaledBottom)
        //val isFrontLens = frame.lensFacing == CameraSelector.LensFacing.FRONT
        return FaceBounds(
            trackingId,
            scaledBoundingBox
        )
    }

    private fun onError(exception: Exception) {
        onFaceDetectionResult?.onFailure(exception)
        Log.e("TAG", "Error Detect Face", exception)
    }


    //inteface -> declaration of abstract methode [Interface cannot store statae, but they can have properties
    interface  OnFaceDetectionResult {
        fun onSuccess(faceBounds: List<FaceBounds>) {
            Log.d("TAG", "Success Detect.....")
        }
        fun onFailure(exception: Exception) {
            Log.e("TAG", "Failed detect....")
        }
    }

    companion object {
        private const val MIN_FACE_SIZE = 0.15F
    }
}