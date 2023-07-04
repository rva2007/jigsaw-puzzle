package com.example.jigsawpuzzles.extentions

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.example.jigsawpuzzles.R

class PuzzlePathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var numberByDefault: Int = 4
    var num: Int = numberByDefault
    private var paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = resources.getColor(R.color.brown, null)
        strokeWidth = 4f
        isAntiAlias = true
    }
    private var path: Path = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path = preparePath(num)
        canvas?.drawPath(path, paint)
    }

    private fun preparePath(num: Int): Path {
        val columns: Int?
        val rows: Int?
        val bigSideOfPuzzlePathView = 4
        val smallSideOfPuzzlePathView = 3
        val pathOfView = Path()

        if (screenOrientationIsPortrait()) {
            columns = num
            rows = (columns * bigSideOfPuzzlePathView) / smallSideOfPuzzlePathView
        } else {
            rows = num
            columns = (rows * bigSideOfPuzzlePathView) / smallSideOfPuzzlePathView
        }

        //calculate the width and the height of the pieces
        val pieceWidth = measuredWidth / columns
        val pieceHeight = measuredHeight / rows

        //create path of each piece and add it to the result array
        var yCoord = 0 //coordinate "Y" of piece
        for (row in 0 until rows) {
            var xCoord = 0 //coordinate "X" of piece
            for (column in 0 until columns) {
                val bumpSize = pieceHeight / 4
                val pathOfPiece = Path()

                pathOfPiece.moveTo(xCoord.toFloat(), yCoord.toFloat())

                if (row == 0) {
                    //top side piece
                    topSidePiece(pathOfPiece, xCoord, pieceWidth, yCoord)
                } else {
                    //top bump
                    topBump(pathOfPiece, xCoord, pieceWidth, yCoord, bumpSize)
                }
                if (column == columns - 1) {
                    //right side piece
                    rightSidePiece(pathOfPiece, xCoord, pieceWidth, yCoord, pieceHeight)
                } else {
                    //right bump
                    rightBump(pathOfPiece, xCoord, pieceWidth, yCoord, pieceHeight, bumpSize)
                }
                if (row == rows - 1) {
                    //bottom side piece
                    bottomSidePiece(pathOfPiece, xCoord, yCoord, pieceHeight)
                } else {
                    //bottom bump
                    bottomBump(pathOfPiece, xCoord, pieceWidth, yCoord, pieceHeight, bumpSize)
                }
                if (column == 0) {
                    //left side piece
                    pathOfPiece.close()
                } else {
                    //left bump
                    leftBump(pathOfPiece, xCoord, yCoord, pieceHeight, bumpSize)
                    pathOfPiece.close()
                }
                pathOfView.addPath(pathOfPiece)
                pathOfPiece.reset()
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }
        return pathOfView
    }

    private fun leftBump(path: Path, xCoord: Int, yCoord: Int, pieceHeight: Int, bumpSize: Int) {
        path.lineTo(
            xCoord.toFloat(),
            (yCoord + pieceHeight / 3 * 2).toFloat(),
        )
        path.cubicTo(
            (xCoord - bumpSize).toFloat(),
            (yCoord + pieceHeight / 6 * 5).toFloat(),
            (xCoord - bumpSize).toFloat(),
            (yCoord + pieceHeight / 6).toFloat(),
            xCoord.toFloat(),
            (yCoord + pieceHeight / 3).toFloat()
        )
    }

    private fun bottomBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int
    ) {
        path.lineTo(
            (xCoord + pieceWidth / 3 * 2).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth / 6 * 5).toFloat(),
            (yCoord + pieceHeight - bumpSize).toFloat(),
            (xCoord + pieceWidth / 6).toFloat(),
            (yCoord + pieceHeight - bumpSize).toFloat(),
            (xCoord + pieceWidth / 3).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
        path.lineTo(
            xCoord.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    private fun bottomSidePiece(
        path: Path,
        xCoord: Int,
        yCoord: Int,
        pieceHeight: Int
    ) {
        path.lineTo(
            xCoord.toFloat(), yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    private fun rightBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int
    ) {
        path.lineTo(
            (xCoord + pieceWidth).toFloat(),
            (yCoord + pieceHeight / 3).toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth - bumpSize).toFloat(),
            (yCoord + pieceHeight / 6).toFloat(),
            (xCoord + pieceWidth - bumpSize).toFloat(),
            (yCoord + pieceHeight / 6 * 5).toFloat(),
            xCoord.toFloat() + pieceWidth.toFloat(),
            (yCoord + pieceHeight / 3 * 2).toFloat()
        )
        path.lineTo(
            xCoord.toFloat() + pieceWidth.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    private fun rightSidePiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int
    ) {
        path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat() + pieceHeight)
    }

    private fun topBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        bumpSize: Int
    ) {
        path.lineTo((xCoord + pieceWidth / 3).toFloat(), yCoord.toFloat())
        path.cubicTo(
            (xCoord + pieceWidth / 6).toFloat(),
            (yCoord - bumpSize).toFloat(),
            ((xCoord + pieceWidth / 6 * 5)).toFloat(),
            (yCoord - bumpSize).toFloat(),
            (xCoord + pieceWidth / 3 * 2).toFloat(),
            yCoord.toFloat()
        )
        path.lineTo(xCoord.toFloat() + pieceWidth.toFloat(), yCoord.toFloat())
    }

    private fun topSidePiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int
    ) {
        path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat())
    }

    private fun screenOrientationIsPortrait(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }

}