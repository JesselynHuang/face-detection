package com.example.facedetection

import android.util.Size
import androidx.camera.core.CameraSelector

// suppress => given compilation warnings in the annotated element
data class Model (
    @Suppress("ArrayInDataClass") val data: ByteArray?,
            val rotation: Int,
            val size: Size,
            val format: Int,
            val lensFacing: CameraSelector.LensFacing
)