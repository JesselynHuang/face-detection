package com.example.facedetection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

//@JvmOverload -> generate multiple constructor
class Frame @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : View(ctx, attrs){
    private val faceBound = mutableListOf<com.example.facedetection.FaceBounds>()
    private val anchorPaint = Paint()
    private val idPaint = Paint()
    private val boundsPaint = Paint()

    init {
        anchorPaint.color = ContextCompat.getColor(context, android.R.color.holo_blue_light)

        idPaint.color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        idPaint.textSize = 40f

        boundsPaint.style = Paint.Style.STROKE
        boundsPaint.color = ContextCompat.getColor(context, android.R.color.black)
        boundsPaint.strokeWidth = 4f
    }

    internal fun updateFaces(bounds: List<FaceBounds>) {
        faceBound.clear()
        faceBound.addAll(bounds)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        faceBound.forEach { faceBound ->
            canvas.drawAnchor(faceBound.box.center())
            canvas.drawId(faceBound.id.toString(), faceBound.box.center())
            canvas.drawBounds(faceBound.box)
        }
    }

    private fun Canvas.drawAnchor(center: PointF) {
        drawCircle(center.x, center.y, ANCHOR_RADIUS, anchorPaint)
    }

    private fun Canvas.drawId(faceId: String, center: PointF) {
        drawText("face id $faceId", center.x - ID_OFFSET, center.y + ID_OFFSET, idPaint)
    }

    private fun Canvas.drawBounds(box: RectF) {
        drawRect(box, boundsPaint)
    }

    private fun RectF.center(): PointF {
        val centerX = left + (right - left) / 2
        val centerY = top + (bottom - top) / 2
        return PointF(centerX, centerY)
    }

//    public void addFrameProcessor(FrameProcessor )

    // Notes
    // Companion object --> used to define variable and methods / static variable

    companion object {
        // f -> float
        private const val ANCHOR_RADIUS = 10f
        private const val ID_OFFSET = 50f
    }
}