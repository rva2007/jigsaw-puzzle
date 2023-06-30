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

    private var columnsNumberByDefault: Int = 4
    var num: Int = columnsNumberByDefault
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

        if (screenOrientationIsPortrait()) {
            columns = num
            rows = columns * 4 / 3
        } else {
            rows = num
            columns = rows * 4 / 3
        }

        val path1 = Path()

        //calculate the width and the height of the pieces
        val pieceWidth = measuredWidth / columns
        val pieceHeight = measuredHeight / rows

        //create each bitmap piece and add it to the result array
        var yCoord = 0
        for (row in 0 until rows) {
            var xCoord = 0
            for (column in 0 until columns) {

                //draw path
                val bumpSize = pieceHeight / 4
                val path = Path()

                path.moveTo(xCoord.toFloat(), yCoord.toFloat())

                if (row == 0) {
                    //top side piece
                    path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat())
                } else {
                    //top bump
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
                if (column == columns - 1) {
                    //right side piece
                    path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat() + pieceHeight)
                } else {
                    //right bump
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
                if (row == rows - 1) {
                    //bottom side piece
                    path.lineTo(
                        xCoord.toFloat(), yCoord.toFloat() + pieceHeight.toFloat()
                    )
                } else {
                    //bottom bump
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
                if (column == 0) {
                    //left side piece
                    path.close()
                } else {
                    //left bump
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
                    path.close()
                }
                path1.addPath(path)
                path.reset()
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }
        return path1
    }

    private fun screenOrientationIsPortrait(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }

}