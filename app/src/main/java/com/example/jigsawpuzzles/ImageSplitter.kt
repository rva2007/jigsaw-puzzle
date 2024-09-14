package com.example.jigsawpuzzles

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.example.jigsawpuzzles.extentions.NotRightAnglesPath
import com.example.jigsawpuzzles.extentions.PuzzlePathView
import com.example.jigsawpuzzles.extentions.RightAnglesPath

class ImageSplitter(context: Context, private var linesType: Int) {
    private val _context = context

    fun getListOfPuzzles(
        imageView: ImageView,
        number: Int,
        bigSideOfImageView: Int,
        smallSideOfImageView: Int,
        isScreenOrientationPortrait: Boolean
    ): ArrayList<PuzzlePiece> {
        val columns: Int
        val rows: Int

        if (isScreenOrientationPortrait) {
            columns = number
            rows = (columns * bigSideOfImageView) / smallSideOfImageView
        } else {
            rows = number
            columns = (rows * bigSideOfImageView) / smallSideOfImageView
        }
        val piecesNumber = columns * rows
        val pieces = ArrayList<PuzzlePiece>(piecesNumber)
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        //calculate the width and the height of the pieces
        val pieceWidth = (imageView.width / columns) - compensationValue
        val pieceHeight = (imageView.height / rows) - compensationValue

        //create each bitmap piece and add it to the result array
        var yCoord = 0
        for (row in 0 until rows) {
            var xCoord = 0
            for (column in 0 until columns) {
                //calculate offset for each piece
                var offsetX = 0
                var offsetY = 0

                val bumpSize = pieceHeight / fourPartsOfWhole

                var bitmapWidth = pieceWidth
                var bitmapHeight = pieceHeight


                val pair = setBitmapWidthAndOffsetX(
                    column,
                    bitmapWidth,
                    pieceWidth,
                    bumpSize,
                    offsetX,
                    columns
                )
                bitmapWidth = pair.first
                offsetX = pair.second


                val pair1 = setBitmapHeightAndOffsetY(
                    row,
                    bitmapHeight,
                    pieceHeight,
                    bumpSize,
                    offsetY,
                    rows
                )
                bitmapHeight = pair1.first
                offsetY = pair1.second

                //bitmap для отрисовки Path и картинки-подложки
                val pieceBitmap = Bitmap.createBitmap(
                    bitmap,
                    xCoord - offsetX,                      // координаты Х подложки
                    yCoord - offsetY,                     // координата Y подложки
                    bitmapWidth,   // ширина картинки подложки
                    bitmapHeight   // высота картинки подложки
                )

                val piece = PuzzlePiece(_context)
                piece.setImageBitmap(pieceBitmap)
                piece.xCoord = xCoord - offsetX + imageView.left
                piece.yCoord = yCoord - offsetY + imageView.top
                piece.pieceWidth = pieceWidth + offsetX
                piece.pieceHeight = pieceHeight + offsetY

                //this bitmap will hold our final puzzle piece image
                val puzzlePiece = Bitmap.createBitmap(
                    pieceWidth + offsetX + bumpSize, //ширина картинки подложки
                    pieceHeight + offsetY + bumpSize, //высота картинки подложки
                    Bitmap.Config.ARGB_8888
                )

                //draw path
                val canvas = Canvas(puzzlePiece)
                val path = Path()
                path.moveTo(offsetX.toFloat(), offsetY.toFloat())
                if (row == 0) {
                    //top piece side
                    drawTopSideOfPiece(path, offsetX, pieceWidth, offsetY)
                } else if (row % 2 != 0) {
                    //top cave
                    drawTopCave(path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize)
                } else {
                    //top bump
                    drawTopBump(path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize)
                }

                if (column == columns - 1) {
                    //right piece side
                    drawRightSideOfPiece(path, offsetX, pieceWidth, offsetY, pieceHeight)
                } else if (column % 2 != 0) {
                    //right cave
                    drawRightCave(path, offsetX, pieceWidth, offsetY, pieceHeight)
                } else {
                    //right bump
                    drawRightBump(path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize)
                }

                if (row == rows - 1) {
                    //bottom piece side
                    drawBottomSideOfPiece(path, offsetX, pieceHeight, offsetY)
                } else if (row % 2 != 0) {
                    //bottom cave
                    drawBottomCave(path, pieceWidth, offsetX, pieceHeight, offsetY, bumpSize)
                } else {
                    //bottom bump
                    drawBottomBump(path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize)
                }

                if (column == 0) {
                    //left piece side
                    drawLeftSideOfPiece(path)
                } else if (column % 2 != 0) {
                    //left cave
                    drawLeftCave(path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize)
                } else {
                    //left bump
                    drawLeftBump(path, pieceWidth, offsetX, pieceHeight, offsetY, bumpSize)
                }

                //mask the piece
                maskPiece(canvas, path, pieceBitmap)

                drawBorders(canvas, path)

                //set the resulting bitmap to the piece
                piece.setImageBitmap(puzzlePiece)
                pieces.add(piece)
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }
        return pieces
    }

    private fun drawLeftBump(
        path: Path,
        pieceWidth: Int,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
        bumpSize: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawLeftBump(
                    path,
                    pieceWidth,
                    offsetX,
                    pieceHeight,
                    offsetY,
                    bumpSize
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawLeftBump(
                    path,
                    pieceWidth,
                    offsetX,
                    pieceHeight,
                    offsetY,
                    bumpSize
                )
            }
        }
    }

    private fun drawLeftCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawLeftCave(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight,
                    bumpSize
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawLeftCave(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight,
                    bumpSize
                )
            }
        }
    }

    private fun drawLeftSideOfPiece(path: Path) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawLeftSideOfPiece(path)
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawLeftSideOfPiece(path)
            }
        }
    }

    private fun drawBottomBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawBottomBump(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight,
                    bumpSize
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawBottomBump(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight,
                    bumpSize
                )
            }
        }
    }

    private fun drawBottomCave(
        path: Path,
        pieceWidth: Int,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
        bumpSize: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawBottomCave(
                    path,
                    pieceWidth,
                    offsetX,
                    pieceHeight,
                    offsetY,
                    bumpSize
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawBottomCave(
                    path,
                    pieceWidth,
                    offsetX,
                    pieceHeight,
                    offsetY,
                    bumpSize
                )
            }
        }
    }

    private fun drawBottomSideOfPiece(
        path: Path,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawBottomSideOfPiece(
                    path, offsetX, pieceHeight, offsetY
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawBottomSideOfPiece(
                    path, offsetX, pieceHeight, offsetY
                )
            }
        }
    }

    private fun drawRightBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawRightBump(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight,
                    bumpSize
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawRightBump(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight,
                    bumpSize
                )
            }
        }
    }

    private fun drawRightCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawRightCave(
                    path, offsetX, pieceWidth, offsetY, pieceHeight
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawRightCave(
                    path, offsetX, pieceWidth, offsetY, pieceHeight
                )
            }
        }
    }

    private fun drawRightSideOfPiece(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawRightSideOfPiece(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawRightSideOfPiece(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY,
                    pieceHeight
                )
            }
        }
    }

    private fun drawTopBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawTopBump(
                    path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawTopBump(
                    path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize
                )
            }
        }
    }

    private fun drawTopCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawTopCave(
                    path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize
                )
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawTopCave(
                    path, offsetX, pieceWidth, offsetY, pieceHeight, bumpSize
                )
            }
        }
    }

    private fun drawTopSideOfPiece(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
    ) {
        when (linesType) {
            PuzzlePathView.rightAnglesCode -> {
                RightAnglesPath().drawTopSideOfPiece(path, offsetX, pieceWidth, offsetY)
            }
            PuzzlePathView.notRightAnglesCode -> {
                NotRightAnglesPath().drawTopSideOfPiece(
                    path,
                    offsetX,
                    pieceWidth,
                    offsetY
                )
            }

        }
    }

    private fun setBitmapHeightAndOffsetY(
        row: Int,
        bitmapHeight: Int,
        pieceHeight: Int,
        bumpSize: Int,
        offsetY: Int,
        rows: Int,
    ): Pair<Int, Int> {
        var bitmapHeight1 = bitmapHeight
        var offsetY1 = offsetY
        if (row == 0) {
            bitmapHeight1 = pieceHeight + bumpSize
            offsetY1 = 0
        } else if (row % 2 == 0 && row != rows - 1) {
            bitmapHeight1 = pieceHeight + bumpSize + bumpSize
            offsetY1 = bumpSize
        } else if (row % 2 != 0 && row != rows - 1) {
            bitmapHeight1 = pieceHeight + bumpSize + bumpSize
            offsetY1 = bumpSize
        } else if (row % 2 == 0 && row == rows - 1) {
            bitmapHeight1 = pieceHeight + bumpSize
            offsetY1 = bumpSize
        } else {
            bitmapHeight1 = pieceHeight + bumpSize
            offsetY1 = bumpSize
        }
        return Pair(bitmapHeight1, offsetY1)
    }

    private fun setBitmapWidthAndOffsetX(
        column: Int,
        bitmapWidth: Int,
        pieceWidth: Int,
        bumpSize: Int,
        offsetX: Int,
        columns: Int,
    ): Pair<Int, Int> {
        var bitmapWidth1 = bitmapWidth
        var offsetX1 = offsetX
        if (column == 0) {
            bitmapWidth1 = pieceWidth + bumpSize
            offsetX1 = 0
        } else if (column % 2 == 0 && column != columns - 1) {
            bitmapWidth1 = pieceWidth + bumpSize + bumpSize
            offsetX1 = bumpSize
        } else if (column % 2 == 0 && column == columns - 1) {
            bitmapWidth1 = pieceWidth + bumpSize
            offsetX1 = bumpSize
        } else if (column % 2 != 0 && column == columns - 1) {
            bitmapWidth1 = pieceWidth + bumpSize
            offsetX1 = bumpSize
        } else {
            bitmapWidth1 = pieceWidth + bumpSize + bumpSize
            offsetX1 = bumpSize
        }
        return Pair(bitmapWidth1, offsetX1)
    }

    private fun drawBorders(canvas: Canvas, path: Path) {
        drawWhiteBarder(canvas, path)
        drawBlackBorder(canvas, path)
    }

    private fun drawBlackBorder(canvas: Canvas, path: Path) {
        val blackBorder = Paint()
        blackBorder.color = -0x80000000
        blackBorder.style = Paint.Style.STROKE
        blackBorder.strokeWidth = 3.0f
        canvas.drawPath(path, blackBorder)
    }

    private fun drawWhiteBarder(canvas: Canvas, path: Path) {
        val whiteBorder = Paint()
        whiteBorder.color = -0x7f000001
        whiteBorder.style = Paint.Style.STROKE
        whiteBorder.strokeWidth = 8.0f
        canvas.drawPath(path, whiteBorder)
    }

    private fun maskPiece(
        canvas: Canvas,
        path: Path,
        pieceBitmap: Bitmap
    ) {
        val paint = Paint()
        paint.color = -0x10000000
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(pieceBitmap, 0f, 0f, paint)
    }

    companion object {
        const val compensationValue = 2
        const val fourPartsOfWhole = 4
    }

}