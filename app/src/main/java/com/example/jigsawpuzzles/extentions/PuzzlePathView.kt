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

    private var numberForCalculateColumnsAndRowsByDefault: Int = 4
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

    private fun preparePathOfView(num: Int): Path {
        val (columns: Int, rows: Int) = calculateNumberColumnsAndRows(num)
        return getPathOfView(columns, rows)
    }

    private fun calculateNumberColumnsAndRows(num: Int): Pair<Int, Int> {
        val columns: Int?
        val rows: Int?
        val bigSideOfPuzzlePathView = 4
        val smallSideOfPuzzlePathView = 3

        if (isScreenOrientationPortrait()) {
            columns = num
            rows = (columns * bigSideOfPuzzlePathView) / smallSideOfPuzzlePathView
        } else {
            rows = num
            columns = (rows * bigSideOfPuzzlePathView) / smallSideOfPuzzlePathView
        }
        return Pair(columns, rows)
    }

    private fun getPathOfView(columns: Int, rows: Int): Path {
        val pathOfView = Path()

        val pieceWidth = calculatePieceWidth(columns)
        val pieceHeight = calculatePieceHeight(rows)

        //create path of each piece and add it to the result array
        var yCoord = 0 //coordinate "Y" of piece
        for (row in 0 until rows) {
            var xCoord = 0 //coordinate "X" of piece
            for (column in 0 until columns) {
                val bumpSize = pieceHeight / fourPartsOfWhole
                val pathOfPiece = Path()

                pathOfPiece.moveTo(xCoord.toFloat(), yCoord.toFloat())

                if (row == 0) {
                    //top side piece
                    createTopSidePiece(pathOfPiece, xCoord, pieceWidth, yCoord)
                } else {
                    //top bump
                    createTopBump(pathOfPiece, xCoord, pieceWidth, yCoord, bumpSize)
                }
                if (column == columns - 1) {
                    //right side piece
                    createRightSideOfPiece(pathOfPiece, xCoord, pieceWidth, yCoord, pieceHeight)
                } else {
                    //right bump
                    createRightBump(pathOfPiece, xCoord, pieceWidth, yCoord, pieceHeight, bumpSize)
                }
                if (row == rows - 1) {
                    //bottom side piece
                    createBottomSideOfPiece(pathOfPiece, xCoord, yCoord, pieceHeight)
                } else {
                    //bottom bump
                    createBottomBump(pathOfPiece, xCoord, pieceWidth, yCoord, pieceHeight, bumpSize)
                }
                if (column == 0) {
                    //left side piece
                    pathOfPiece.close()
                } else {
                    //left bump
                    createLeftBump(pathOfPiece, xCoord, yCoord, pieceHeight, bumpSize)
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

    private fun calculatePieceHeight(rows: Int) = measuredHeight / rows

    private fun calculatePieceWidth(columns: Int) = measuredWidth / columns

    private fun createLeftBump(
        path: Path,
        xCoord: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int
    ) {
        path.lineTo(
            xCoord.toFloat(),
            (yCoord + pieceHeight / threePartsOfWhole * twoPartsOfWhole).toFloat(),
        )
        path.cubicTo(
            (xCoord - bumpSize).toFloat(),
            (yCoord + pieceHeight / sixPartsOfWhole * fivePartsOfWhole).toFloat(),
            (xCoord - bumpSize).toFloat(),
            (yCoord + pieceHeight / sixPartsOfWhole).toFloat(),
            xCoord.toFloat(),
            (yCoord + pieceHeight / threePartsOfWhole).toFloat()
        )
    }

    private fun createBottomBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int
    ) {
        path.lineTo(
            (xCoord + pieceWidth / threePartsOfWhole * twoPartsOfWhole).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth / sixPartsOfWhole * fivePartsOfWhole).toFloat(),
            (yCoord + pieceHeight - bumpSize).toFloat(),
            (xCoord + pieceWidth / sixPartsOfWhole).toFloat(),
            (yCoord + pieceHeight - bumpSize).toFloat(),
            (xCoord + pieceWidth / threePartsOfWhole).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
        path.lineTo(
            xCoord.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    private fun createBottomSideOfPiece(
        path: Path,
        xCoord: Int,
        yCoord: Int,
        pieceHeight: Int
    ) {
        path.lineTo(
            xCoord.toFloat(), yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    private fun createRightBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int
    ) {
        path.lineTo(
            (xCoord + pieceWidth).toFloat(),
            (yCoord + pieceHeight / threePartsOfWhole).toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth - bumpSize).toFloat(),
            (yCoord + pieceHeight / sixPartsOfWhole).toFloat(),
            (xCoord + pieceWidth - bumpSize).toFloat(),
            (yCoord + pieceHeight / sixPartsOfWhole * fivePartsOfWhole).toFloat(),
            xCoord.toFloat() + pieceWidth.toFloat(),
            (yCoord + pieceHeight / threePartsOfWhole * twoPartsOfWhole).toFloat()
        )
        path.lineTo(
            xCoord.toFloat() + pieceWidth.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    private fun createRightSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int
    ) {
        path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat() + pieceHeight)
    }

    private fun createTopBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        bumpSize: Int
    ) {
        path.lineTo((xCoord + pieceWidth / threePartsOfWhole).toFloat(), yCoord.toFloat())
        path.cubicTo(
            (xCoord + pieceWidth / sixPartsOfWhole).toFloat(),
            (yCoord - bumpSize).toFloat(),
            ((xCoord + pieceWidth / sixPartsOfWhole * fivePartsOfWhole)).toFloat(),
            (yCoord - bumpSize).toFloat(),
            (xCoord + pieceWidth / threePartsOfWhole * twoPartsOfWhole).toFloat(),
            yCoord.toFloat()
        )
        path.lineTo(xCoord.toFloat() + pieceWidth.toFloat(), yCoord.toFloat())
    }

    private fun createTopSidePiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int
    ) {
        path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat())
    }

    private fun isScreenOrientationPortrait(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }

    companion object {
        const val twoPartsOfWhole = 2
        const val threePartsOfWhole = 3
        const val fourPartsOfWhole = 4
        const val fivePartsOfWhole = 5
        const val sixPartsOfWhole = 6
    }


}