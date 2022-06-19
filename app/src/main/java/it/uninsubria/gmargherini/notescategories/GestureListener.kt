package it.uninsubria.gmargherini.notescategories

import android.view.GestureDetector
import android.view.MotionEvent

class GestureListener:GestureDetector.SimpleOnGestureListener() {
    override fun onContextClick(e: MotionEvent?): Boolean {
        return super.onContextClick(e)
        
    }
}