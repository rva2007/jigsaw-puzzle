package com.example.jigsawpuzzles

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

class ImageSplitter(context: Context) {
    private val settingsActivity = SettingsActivity()
    private val _context = context

    fun onImageSplit(
        imageView: ImageView?,
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
        val drawable = imageView?.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        //calculate the width and the height of the pieces
        val pieceWidth = imageView.width / columns
        val pieceHeight = imageView.height / rows

        //create each bitmap piece and add it to the result array
        var yCoord = 0
        for (row in 0 until rows) {
            var xCoord = 0
            for (column in 0 until columns) {
                //calculate offset for each piece
                var offsetX = 0
                var offsetY = 0
                if (column > 0) {
                    offsetX = pieceWidth / 3
                }
                if (row > 0) {
                    offsetY = pieceHeight / 3
                }
                val pieceBitmap = Bitmap.createBitmap(
                    bitmap,
                    xCoord - offsetX,
                    yCoord - offsetY,
                    pieceWidth + offsetX,
                    pieceHeight + offsetY
                )
                val piece = PuzzlePiece(_context)
                piece.setImageBitmap(pieceBitmap)
                piece.dataOfPiece.xCoord = xCoord - offsetX + imageView.left
                piece.dataOfPiece.yCoord = yCoord - offsetY + imageView.top
                piece.dataOfPiece.pieceWidth = pieceWidth + offsetX
                piece.dataOfPiece.pieceHeight = pieceHeight + offsetY
                //this bitmap will hold our final puzzle piece image
                val puzzlePiece = Bitmap.createBitmap(
                    pieceWidth + offsetX, pieceHeight + offsetY, Bitmap.Config.ARGB_8888
                )
//                //draw path
                val bumpSize = pieceHeight / 4
                val canvas = Canvas(puzzlePiece)
                val path = Path()
                path.moveTo(offsetX.toFloat(), offsetY.toFloat())

                if (row == 0) {
                    //top piece side
                    drawTopPieceSide(path, pieceBitmap, offsetY)
                } else {
                    //top bump
                    drawTopBump(path, offsetX, pieceBitmap, offsetY, bumpSize)
                }
                if (column == columns - 1) {
                    //right piece side
                    drawRightPieceSide(path, pieceBitmap)
                } else {
                    //right bump
                    drawRightBump(path, pieceBitmap, offsetY, bumpSize)
                }
                if (row == rows - 1) {
                    //bottom piece side
                    drawBottomPieceSide(path, offsetX, pieceBitmap)
                } else {
                    //bottom bump
                    drawBottomBump(path, offsetX, pieceBitmap, bumpSize)
                }
                if (column == 0) {
                    //left piece side
                    path.close()
                } else {
                    //left bump
                    drawLeftBump(path, offsetX, offsetY, pieceBitmap, bumpSize)
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

    private fun drawBorders(canvas: Canvas, path: Path) {
        //draw a white border
        drawWhiteBarder(canvas, path)

        //draw a black border
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

    private fun drawBottomBump(
        path: Path,
        offsetX: Int,
        pieceBitmap: Bitmap,
        bumpSize: Int
    ) {
        path.lineTo(
            (offsetX + (pieceBitmap.width - offsetX) / 3 * 2).toFloat(),
            pieceBitmap.height.toFloat()
        )
        path.cubicTo(
            (offsetX + (pieceBitmap.width - offsetX) / 6 * 5).toFloat(),
            (pieceBitmap.height - bumpSize).toFloat(),
            (offsetX + (pieceBitmap.width - offsetX) / 6).toFloat(),
            (pieceBitmap.height - bumpSize).toFloat(),
            (offsetX + (pieceBitmap.width - offsetX) / 3).toFloat(),
            pieceBitmap.height.toFloat()
        )
        path.lineTo(
            offsetX.toFloat(),
            pieceBitmap.height.toFloat()
        )
    }

    private fun drawLeftBump(
        path: Path,
        offsetX: Int,
        offsetY: Int,
        pieceBitmap: Bitmap,
        bumpSize: Int
    ) {
        path.lineTo(
            offsetX.toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 3 * 2).toFloat(),
        )
        path.cubicTo(
            (offsetX - bumpSize).toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 6 * 5).toFloat(),
            (offsetX - bumpSize).toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 6).toFloat(),
            offsetX.toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 3).toFloat()
        )
        path.close()
    }

    private fun drawRightBump(
        path: Path,
        pieceBitmap: Bitmap,
        offsetY: Int,
        bumpSize: Int
    ) {
        path.lineTo(
            pieceBitmap.width.toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 3).toFloat()
        )
        path.cubicTo(
            (pieceBitmap.width - bumpSize).toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 6).toFloat(),
            (pieceBitmap.width - bumpSize).toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 6 * 5).toFloat(),
            pieceBitmap.width.toFloat(),
            (offsetY + (pieceBitmap.height - offsetY) / 3 * 2).toFloat()
        )
        path.lineTo(
            pieceBitmap.width.toFloat(),
            pieceBitmap.height.toFloat()
        )
    }

    private fun drawRightPieceSide(path: Path, pieceBitmap: Bitmap) {
        path.lineTo(
            pieceBitmap.width.toFloat(),
            pieceBitmap.height.toFloat()
        )
    }

    private fun drawBottomPieceSide(
        path: Path,
        offsetX: Int,
        pieceBitmap: Bitmap
    ) {
        path.lineTo(
            offsetX.toFloat(), pieceBitmap.height.toFloat()
        )
    }

    private fun drawTopBump(
        path: Path,
        offsetX: Int,
        pieceBitmap: Bitmap,
        offsetY: Int,
        bumpSize: Int
    ) {
        path.lineTo(
            (offsetX + (pieceBitmap.width - offsetX) / 3).toFloat(),
            offsetY.toFloat()
        )
        path.cubicTo(
            ((offsetX + (pieceBitmap.width - offsetX) / 6).toFloat()),
            (offsetY - bumpSize).toFloat(),
            ((offsetX + (pieceBitmap.width - offsetX) / 6 * 5)).toFloat(),
            (offsetY - bumpSize).toFloat(),
            (offsetX + (pieceBitmap.width - offsetX) / 3 * 2).toFloat(),
            offsetY.toFloat()
        )
        path.lineTo(pieceBitmap.width.toFloat(), offsetY.toFloat())
    }

    private fun drawTopPieceSide(
        path: Path,
        pieceBitmap: Bitmap,
        offsetY: Int
    ) {
        path.lineTo(
            pieceBitmap.width.toFloat(),
            offsetY.toFloat()
        )
    }


}