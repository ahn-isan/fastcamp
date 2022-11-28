package com.example.aop_part02_chapter07

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class SoundVisualizerView(context: Context, attrs: AttributeSet? = null): View(context, attrs){

    var onRequestCurrentAmplitude: (()->Int)? = null

    // ANTI_ALIAS_FLAG 더 곡선스럽게 보여지는 방법
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }
    private var drawingWidth:Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    private var isReplaying: Boolean = false
    private var replayingPosition:Int = 0

    private val visualizeRepeatAction: Runnable = object :Runnable{
        override fun run() {
            if(!isReplaying){
            val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
            //Amplitude, Draw
            drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            }else{
                replayingPosition++
            }
            invalidate()

            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let {  amplitudes ->
                if (isReplaying){
                    amplitudes.takeLast(replayingPosition)
                }else{
                    amplitudes
                }
            }
            .forEach { amplitude ->
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

            offsetX -= LINE_SPACE
            if (offsetX < 0 ) return@forEach

            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2F,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
            )
        }

    }
    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }
    fun stopVisualizing(){
        handler?.removeCallbacks(visualizeRepeatAction)
    }

        companion object{
            private const val LINE_WIDTH = 10F
            private const val LINE_SPACE = 15F
            private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
            private const val ACTION_INTERVAL = 20L
    }
}