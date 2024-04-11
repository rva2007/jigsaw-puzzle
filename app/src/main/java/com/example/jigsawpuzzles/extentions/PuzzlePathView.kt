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
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var linesType:Int = rightAngles

    var num: Int = numberForCalculateColumnsAndRowsByDefault
    private var paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = resources.getColor(R.color.brown, null)
        strokeWidth = 4f
        isAntiAlias = true
    }
    private var path: Path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path = preparePathOfView(num)
        canvas.drawPath(path, paint)
    }

    fun preparePathOfView(num: Int): Path {
        val (columns: Int, rows: Int) = calculateNumberColumnsAndRows(num)
        return getPathOfView(columns, rows, linesType)
    }

    fun calculateNumberColumnsAndRows(num: Int): Pair<Int, Int> {
        val columns: Int?
        val rows: Int?

        if (isScreenOrientationPortrait()) {
            columns = num
            rows = (columns * bigSideOfPuzzlePathView) / smallSideOfPuzzlePathView
        } else {
            rows = num
            columns = (rows * bigSideOfPuzzlePathView) / smallSideOfPuzzlePathView
        }
        return Pair(columns, rows)
    }

    fun calculatePieceHeight(rows: Int) = measuredHeight / rows

    fun calculatePieceWidth(columns: Int) = measuredWidth / columns

    private fun getPathOfView(columns: Int, rows: Int, linesType: Int): Path {
        val pathOfView = Path()

        val pieceWidth = calculatePieceWidth(columns)
        val pieceHeight = calculatePieceHeight(rows)

        //create path of each piece and add it to the result array
        var yCoord = 0 //coordinate "Y" of piece
        for (row in 0 until rows) {
            var xCoord = 0 //coordinate "X" of piece
            for (column in 0 until columns) {
                val bumpSize = pieceWidth / four
                val pathOfPiece = Path()

                pathOfPiece.moveTo(xCoord.toFloat(), yCoord.toFloat())

                if (row == 0) {
                    //top piece side
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createTopSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord
                        )
                        notRightAngles -> NotRightAnglesPath().createTopSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord
                        )
                    }
                } else if (row % 2 != 0) {
                    //top cave
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createTopCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createTopCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                } else {
                    //top bump
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createTopBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createTopBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                }
                if (column == columns - 1) {
                    //right piece side
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createRightSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight
                        )
                        notRightAngles -> NotRightAnglesPath().createRightSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight
                        )
                    }
                } else if (column % 2 != 0) {
                    //right cave
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createRightCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createRightCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                } else {
                    //right bump
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createRightBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createRightBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                }
                if (row == rows - 1) {
                    //bottom piece side
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createBottomSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            yCoord,
                            pieceHeight
                        )
                        notRightAngles -> NotRightAnglesPath().createBottomSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            yCoord,
                            pieceHeight
                        )
                    }
                } else if (row % 2 != 0) {
                    //bottom cave
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createBottomCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createBottomCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                } else {
                    //bottom bump
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createBottomBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createBottomBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                }
                if (column == 0) {
                    //left piece side
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createLeftSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight
                        )
                        notRightAngles -> NotRightAnglesPath().createLeftSideOfPiece(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight
                        )
                    }
                } else if (column % 2 != 0) {
                    //left cave
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createLeftCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createLeftCave(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                } else {
                    //left bump
                    when (linesType) {
                        rightAngles -> RightAnglesPath().createLeftBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                        notRightAngles -> NotRightAnglesPath().createLeftBump(
                            pathOfPiece,
                            xCoord,
                            pieceWidth,
                            yCoord,
                            pieceHeight,
                            bumpSize
                        )
                    }
                }
                pathOfView.addPath(pathOfPiece)
                pathOfPiece.reset()
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }
        return pathOfView
    }


    fun isScreenOrientationPortrait(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }

    companion object {

        const val numberForCalculateColumnsAndRowsByDefault: Int = 4
        const val bigSideOfPuzzlePathView = 4
        const val smallSideOfPuzzlePathView = 3
        const val two = 2
        const val three = 3
        const val four = 4
        const val five = 5
        const val six = 6
        const val rightAngles = 0
        const val notRightAngles = 1

    }


}