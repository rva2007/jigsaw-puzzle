package com.example.jigsawpuzzles

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.widget.RelativeLayout
import kotlin.random.Random

class AnimationReturnOfPieces(
    val context: Context,
    val piece: PuzzlePiece,
    val containerLayout: RelativeLayout
) {

    val lParams = piece.layoutParams as RelativeLayout.LayoutParams

    fun showAnimationForOrientationLandscape() {

        val translateX = (containerLayout.width - (piece.width + piece.getX()))
        val tempY = piece.getY()
        val tempX = piece.getX()
        val positionAnimator = ValueAnimator.ofFloat(translateX)
        positionAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            piece.translationX = value
        }

        positionAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                GameSounds(context).playSoundEndOfMovement()
                lParams.leftMargin = (tempX + translateX).toInt()
                piece.layoutParams = lParams
                piece.setX(tempX)
                piece.setY(tempY)
            }
        })

        val rotationAnimator = ObjectAnimator.ofFloat(piece, "rotation", 0f, 360f)
        val animatorSet = AnimatorSet()

        animatorSet.play(positionAnimator).with(rotationAnimator)
        animatorSet.duration = Random.nextLong(1000)
        animatorSet.start()
    }

    fun showAnimationForOrientationPortrait() {
        val translateY = (containerLayout.height - (piece.height + piece.getY()))
        val tempY = piece.getY()
        val tempX = piece.getX()

        val positionAnimator = ValueAnimator.ofFloat(translateY.toFloat())
        positionAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            piece.translationY = value
        }

        positionAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                GameSounds(context).playSoundEndOfMovement()
                lParams.topMargin = (tempY + translateY).toInt()
                piece.layoutParams = lParams
                piece.setX(tempX)
                piece.setY(tempY)
            }
        })

        val rotationAnimator = ObjectAnimator.ofFloat(piece, "rotation", 0f, 360f)
        val animatorSet = AnimatorSet()

        animatorSet.play(positionAnimator).with(rotationAnimator)
        animatorSet.duration = Random.nextLong(1000)
        animatorSet.start()
    }

}