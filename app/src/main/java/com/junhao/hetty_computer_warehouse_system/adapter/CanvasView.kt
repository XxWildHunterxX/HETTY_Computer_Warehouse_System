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
            //vertical
            canvas?.drawLine(540f,400f,540f,530f,paint)
            //horizontal
            canvas?.drawLine(540f,530f,380f,530f,paint)
            //v
            canvas?.drawLine(380f,530f,380f,950f,paint)
            //h
            canvas?.drawLine(380f,950f,320f,950f,paint)
        }else if(getRack!![0] == "B" && getRack!![1].toInt() < 51){
            //vertical
            canvas?.drawLine(540f,400f,540f,530f,paint)
            //horizontal
            canvas?.drawLine(540f,530f,350f,530f,paint)
            //v
            canvas?.drawLine(350f,530f,350f,950f,paint)
            //h
            canvas?.drawLine(350f,950f,410f,950f,paint)
        }else if(getRack!![0] == "B" && getRack!![1].toInt() > 51){
            //vertical
            canvas?.drawLine(540f,400f,540f,530f,paint)
            //horizontal
            canvas?.drawLine(540f,530f,715f,530f,paint)
            //v
            canvas?.drawLine(715f,530f,715f,950f,paint)
            //h
            canvas?.drawLine(715f,950f,660f,950f,paint)
        }else if(getRack!![0] == "C" && getRack!![1].toInt() < 51){
            //vertical
            canvas?.drawLine(540f,400f,540f,530f,paint)
            //horizontal
            canvas?.drawLine(540f,530f,715f,530f,paint)
            //v
            canvas?.drawLine(715f,530f,715f,950f,paint)
            //h
            canvas?.drawLine(715f,950f,750f,950f,paint)
        }else if(getRack!![0] == "C" && getRack!![1].toInt() > 51){
            //vertical
            canvas?.drawLine(540f,400f,540f,530f,paint)
            //horizontal
            canvas?.drawLine(540f,530f,1050f,530f,paint)
            //v
            canvas?.drawLine(1050f,530f,1050f,950f,paint)
            //h
            canvas?.drawLine(1050f,950f,1000f,950f,paint)
        }

    }

}