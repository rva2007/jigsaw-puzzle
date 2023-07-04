package com.example.jigsawpuzzles

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PPData(
    var id: Int = 0,
    var x: Int = 0,
    var y: Int = 0,
    var xCoord: Int = 0,
    var yCoord: Int = 0,
    var pieceWidth: Int = 0,
    var pieceHeight: Int = 0,
    var canMove: Boolean = true,
    var bitmap: Bitmap? = null
) : Parcelable