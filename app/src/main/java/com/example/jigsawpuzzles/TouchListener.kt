package com.example.jigsawpuzzles

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import kotlin.math.pow
import kotlin.math.sqrt

class TouchListener(private val activity: SettingsActivity) : View.OnTouchListener {
    private var xDelta = 0f
    private var yDelta = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val x = motionEvent!!.rawX
        val y = motionEvent.rawY
        val tolerance = sqrt(
            view!!.width.toDouble().pow(2.0) +
                    view.height.toDouble().pow(2.0)
        ) / 10

        val piece = view as PuzzlePiece

        if (!piece.pieceData.canMove) {
            return true
        }

        val lParams = view.layoutParams as RelativeLayout.LayoutParams

        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xDelta = x - lParams.leftMargin
                yDelta = y - lParams.topMargin
                piece.bringToFront()
            }

            MotionEvent.ACTION_MOVE -> {
                lParams.leftMargin = (x - xDelta).toInt()
                lParams.topMargin = (y - yDelta).toInt()
                view.layoutParams = lParams
            }

            MotionEvent.ACTION_UP -> {
                val xDiff = StrictMath.abs(
                    piece.pieceData.xCoord - lParams.leftMargin
                )
                val yDiff = StrictMath.abs(
                    piece.pieceData.yCoord - lParams.topMargin
                )
                piece.pieceData.x = piece.x.toInt()
                piece.pieceData.y = piece.y.toInt()

                if (xDiff <= tolerance && yDiff <= tolerance) {
                    lParams.leftMargin = piece.pieceData.xCoord
                    lParams.topMargin = piece.pieceData.yCoord
                    piece.layoutParams = lParams
                    piece.pieceData.canMove = false
                    sendViewToBack(piece)
                    activity.checkGameOver()
                }
            }
        }
        return true
    }

    private fun sendViewToBack(child: View) {
        val parent = child.parent as ViewGroup
        parent.removeView(child)
        parent.addView(child, 0)
    }
}