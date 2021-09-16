package com.junhao.hetty_computer_warehouse_system.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences




class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint : Paint = Paint()

        paint.color = Color.BLUE
        paint.strokeWidth=10f

        val prefs = context.getSharedPreferences("sharedPrefsRackLocation", MODE_PRIVATE)
        val loadedString = prefs.getString("getRackLocation", null)

        val getRack = loadedString?.split("-")

        if(getRack!![0] == "A" && getRack!![1].toInt() < 51){
            //vertical
            canvas?.drawLine(540f,400f,540f,530f,paint)
            //horizontal
            canvas?.drawLine(540f,530f,30f,530f,paint)
            //v
            canvas?.drawLine(30f,530f,30f,950f,paint)
            //h
            canvas?.drawLine(30f,950f,70f,950f,paint)
        }else if(getRack!![0] == "A" && getRack!![1].toInt() > 51){

        }

    }

}