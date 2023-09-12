package com.example.jigsawpuzzles

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView

class PuzzlePiece(context: Context?) : AppCompatImageView(context!!) {
    var x: Int = 0
    var y: Int = 0
    var xCoord: Int = 0
    var yCoord: Int = 0
    var pieceWidth: Int = 0
    var pieceHeight: Int = 0
    var canMove: Boolean = true
}