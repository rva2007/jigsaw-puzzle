package com.example.jigsawpuzzles.extentions

import android.graphics.Path

class NotRightAnglesPath:InterfacePuzzlePathView {
    override fun createLeftBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord + pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight / two).toFloat(),
            xCoord.toFloat(),
            (yCoord + pieceHeight / three * two).toFloat(),
        )
        path.cubicTo(
            (xCoord - bumpSize).toFloat(),
            (yCoord + pieceHeight / six * five).toFloat(),
            (xCoord - bumpSize).toFloat(),
            (yCoord + pieceHeight / six).toFloat(),
            xCoord.toFloat(),
            (yCoord + pieceHeight / three).toFloat()
        )
        path.quadTo(
            (xCoord + pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight / two).toFloat(),
            xCoord.toFloat(),
            yCoord.toFloat()
        )

    }

    override fun createLeftCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord - pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight / two).toFloat(),
            xCoord.toFloat(),
            (yCoord + pieceHeight / three * two).toFloat(),
        )
        path.cubicTo(
            (xCoord + bumpSize).toFloat(),
            (yCoord + pieceHeight / six * five).toFloat(),
            (xCoord + bumpSize).toFloat(),
            (yCoord + pieceHeight / six).toFloat(),
            xCoord.toFloat(),
            (yCoord + pieceHeight / three).toFloat()
        )
        path.quadTo(
            (xCoord - pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight / two).toFloat(),
            xCoord.toFloat(),
            yCoord.toFloat()
        )
    }

    override fun createBottomCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord + pieceWidth - pieceWidth / two).toFloat(),
            (yCoord + pieceHeight + pieceHeight / ten * two).toFloat(),
            (xCoord + pieceWidth / three * two).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )

        path.cubicTo(
            (xCoord + pieceWidth / six * five).toFloat(),
            (yCoord + pieceHeight - bumpSize).toFloat(),
            (xCoord + pieceWidth / six).toFloat(),
            (yCoord + pieceHeight - bumpSize).toFloat(),
            (xCoord + pieceWidth / three).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )

        path.quadTo(
            (xCoord + pieceWidth - pieceWidth / two).toFloat(),
            (yCoord + pieceHeight + pieceHeight / ten * two).toFloat(),
            xCoord.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    override fun createBottomBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord + pieceWidth - pieceWidth / two).toFloat(),
            (yCoord + pieceHeight - pieceHeight / ten * two).toFloat(),
            (xCoord + pieceWidth / three * two).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )

        path.cubicTo(
            (xCoord + pieceWidth / six * five).toFloat(),
            (yCoord + pieceHeight + bumpSize).toFloat(),
            (xCoord + pieceWidth / six).toFloat(),
            (yCoord + pieceHeight + bumpSize).toFloat(),
            (xCoord + pieceWidth / three).toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )

        path.quadTo(
            (xCoord + pieceWidth - pieceWidth / two).toFloat(),
            (yCoord + pieceHeight - pieceHeight / ten * two).toFloat(),
            xCoord.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    override fun createBottomSideOfPiece(path: Path, xCoord: Int, yCoord: Int, pieceHeight: Int) {
        path.lineTo(
            xCoord.toFloat(), yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    override fun createRightBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord + pieceWidth - pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight / two).toFloat(),
            (xCoord + pieceWidth).toFloat(),
            (yCoord + pieceHeight / three).toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth + bumpSize).toFloat(),
            (yCoord + pieceHeight / six).toFloat(),
            (xCoord + pieceWidth + bumpSize).toFloat(),
            (yCoord + pieceHeight / six * five).toFloat(),
            xCoord.toFloat() + pieceWidth.toFloat(),
            (yCoord + pieceHeight / three * two).toFloat()
        )
        path.quadTo(
            (xCoord + pieceWidth - pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight - pieceHeight / two).toFloat(),
            xCoord.toFloat() + pieceWidth.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    override fun createRightCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord + pieceWidth + pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight / two).toFloat(),
            (xCoord + pieceWidth).toFloat(),
            (yCoord + pieceHeight / three).toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth - bumpSize).toFloat(),
            (yCoord + pieceHeight / six).toFloat(),
            (xCoord + pieceWidth - bumpSize).toFloat(),
            (yCoord + pieceHeight / six * five).toFloat(),
            xCoord.toFloat() + pieceWidth.toFloat(),
            (yCoord + pieceHeight / three * two).toFloat()
        )
        path.quadTo(
            (xCoord + pieceWidth + pieceWidth / ten * two).toFloat(),
            (yCoord + pieceHeight - pieceHeight / two).toFloat(),
            xCoord.toFloat() + pieceWidth.toFloat(),
            yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    override fun createRightSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
    ) {
        path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat() + pieceHeight)
    }

    override fun createTopSideOfPiece(path: Path, xCoord: Int, pieceWidth: Int, yCoord: Int) {
        path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat())
    }

    override fun createTopBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord + pieceWidth / two).toFloat(),
            (yCoord + pieceHeight / ten * two).toFloat(),
            (xCoord + pieceWidth / three).toFloat(),
            yCoord.toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth / six).toFloat(),
            (yCoord - bumpSize).toFloat(),
            ((xCoord + pieceWidth / six * five)).toFloat(),
            (yCoord - bumpSize).toFloat(),
            (xCoord + pieceWidth / three * two).toFloat(),
            yCoord.toFloat()
        )
        path.quadTo(
            (xCoord + pieceWidth / two).toFloat(),
            (yCoord + pieceHeight / ten * two).toFloat(),
            (xCoord + pieceWidth).toFloat(),
            yCoord.toFloat()
        )
    }

    override fun createTopCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (xCoord + pieceWidth / two).toFloat(),
            (yCoord - pieceHeight / ten * two).toFloat(),
            (xCoord + pieceWidth / three).toFloat(),
            yCoord.toFloat()
        )
        path.cubicTo(
            (xCoord + pieceWidth / six).toFloat(),
            (yCoord + bumpSize).toFloat(),
            ((xCoord + pieceWidth / six * five)).toFloat(),
            (yCoord + bumpSize).toFloat(),
            (xCoord + pieceWidth / three * two).toFloat(),
            yCoord.toFloat()
        )
        path.quadTo(
            (xCoord + pieceWidth / two).toFloat(),
            (yCoord - pieceHeight / ten * two).toFloat(),
            (xCoord + pieceWidth).toFloat(),
            yCoord.toFloat()
        )
    }

    override fun createLeftSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
    ) {
        path.close()
    }

    companion object{
        const val two = 2
        const val three = 3
        const val five = 5
        const val six = 6
        const val ten = 10

    }
}