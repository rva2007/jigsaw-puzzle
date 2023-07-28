package com.example.jigsawpuzzles

import android.annotation.SuppressLint
import android.media.MediaPlayer
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

        if (!piece.dataOfPiece.canMove) {
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
                    piece.dataOfPiece.xCoord - lParams.leftMargin
                )
                val yDiff = StrictMath.abs(
                    piece.dataOfPiece.yCoord - lParams.topMargin
                )
                piece.dataOfPiece.x = piece.x.toInt()
                piece.dataOfPiece.y = piece.y.toInt()

                if (xDiff <= tolerance && yDiff <= tolerance) {
                    playSoundFit()
                    lParams.leftMargin = piece.dataOfPiece.xCoord
                    lParams.topMargin = piece.dataOfPiece.yCoord
                    piece.layoutParams = lParams
                    piece.dataOfPiece.canMove = false
                    sendViewToBack(piece)
                    activity.checkGameOver()
                }
            }
        }
        return true
    }

    private fun playSoundFit() {
        val fitSound = MediaPlayer.create(this.activity, R.raw.fit_sound)
        fitSound.start()
    }

    private fun sendViewToBack(child: View) {
        val parent = child.parent as ViewGroup
        parent.removeView(child)
        parent.addView(child, 0)
    }
}