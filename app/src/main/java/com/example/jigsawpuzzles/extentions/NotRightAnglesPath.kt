package com.example.jigsawpuzzles.extentions

import android.graphics.Path

class NotRightAnglesPath : InterfacePuzzlePathView {
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

    override fun drawLeftBump(
        path: Path,
        pieceWidth: Int,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (offsetX + pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight / two).toFloat(),
            offsetX.toFloat(),
            (offsetY + pieceHeight / three * two).toFloat()
        )
        path.cubicTo(
            (offsetX - bumpSize).toFloat(),
            (offsetY + pieceHeight / six * five).toFloat(),
            (offsetX - bumpSize).toFloat(),
            (offsetY + (pieceHeight - offsetY) / six).toFloat(),
            offsetX.toFloat(),
            (offsetY + pieceHeight / three).toFloat()
        )
        path.quadTo(
            (offsetX + pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight / two).toFloat(),
            offsetX.toFloat(),
            offsetY.toFloat()
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

    override fun drawLeftCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (offsetX - pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight / two).toFloat(),
            offsetX.toFloat(),
            (offsetY + pieceHeight / three * two).toFloat(),
        )
        path.cubicTo(
            (offsetX + bumpSize).toFloat(),
            (offsetY + pieceHeight / six * five).toFloat(),
            (offsetX + bumpSize).toFloat(),
            (offsetY + (pieceHeight - offsetY) / six).toFloat(),
            offsetX.toFloat(),
            (offsetY + pieceHeight / three).toFloat()
        )
        path.quadTo(
            (offsetX - pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight / two).toFloat(),
            offsetX.toFloat(),
            offsetY.toFloat()
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

    override fun drawBottomCave(
        path: Path,
        pieceWidth: Int,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (offsetX + pieceWidth - pieceWidth / two).toFloat(),
            (offsetY + pieceHeight + pieceHeight / ten * two).toFloat(),
            (offsetX + pieceWidth / three * two).toFloat(),
            (offsetY + pieceHeight).toFloat()
        )
        path.cubicTo(
            (offsetX + pieceWidth / six * five).toFloat(),
            (offsetY + pieceHeight - bumpSize).toFloat(),
            (offsetX + pieceWidth / six).toFloat(),
            (offsetY + pieceHeight - bumpSize).toFloat(),
            (offsetX + pieceWidth / three).toFloat(),
            (offsetY + pieceHeight).toFloat()
        )
        path.quadTo(
            (offsetX + pieceWidth / two).toFloat(),
            (offsetY + pieceHeight + pieceHeight / ten * two).toFloat(),
            offsetX.toFloat(),
            (offsetY + pieceHeight).toFloat()
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

    override fun drawBottomBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (offsetX + pieceWidth - pieceWidth / two).toFloat(),
            (offsetY + pieceHeight - pieceHeight / ten * two).toFloat(),
            (offsetX + pieceWidth / three * two).toFloat(),
            pieceHeight.toFloat() + offsetY
        )
        path.cubicTo(
            (offsetX + offsetX + (pieceWidth - offsetX) / six * five).toFloat(),
            (offsetY + pieceHeight + bumpSize).toFloat(),
            (offsetX + (pieceWidth - offsetX) / six).toFloat(),
            (offsetY + pieceHeight + bumpSize).toFloat(),
            (offsetX + pieceWidth / three).toFloat(),
            pieceHeight.toFloat() + offsetY
        )
        path.quadTo(
            (offsetX + pieceWidth - pieceWidth / two).toFloat(),
            (offsetY + pieceHeight - pieceHeight / ten * two).toFloat(),
            offsetX.toFloat(),
            (offsetY + pieceHeight).toFloat()
        )

    }

    override fun createBottomSideOfPiece(path: Path, xCoord: Int, yCoord: Int, pieceHeight: Int) {
        path.lineTo(
            xCoord.toFloat(), yCoord.toFloat() + pieceHeight.toFloat()
        )
    }

    override fun drawBottomSideOfPiece(path: Path, offsetX: Int, pieceHeight: Int, offsetY: Int) {
        path.lineTo(
            offsetX.toFloat(),
            (offsetY + pieceHeight).toFloat()
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

    override fun drawRightBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (offsetX + pieceWidth - pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight / two).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            (offsetY + pieceHeight / three).toFloat()
        )
        path.cubicTo(
            (offsetX + pieceWidth + bumpSize).toFloat(),
            (offsetY + (pieceHeight - offsetY) / six).toFloat(),
            (offsetX + pieceWidth + bumpSize).toFloat(),
            (offsetY + pieceHeight / six * five).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            (offsetY + pieceHeight / three * two).toFloat()
        )
        path.quadTo(
            (offsetX + pieceWidth - pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight - pieceHeight / two).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            (offsetY + pieceHeight).toFloat()
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

    override fun drawRightCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
    ) {
        path.quadTo(
            (offsetX + pieceWidth + pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight / two).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            (offsetY + pieceHeight / three).toFloat()
        )
        path.cubicTo(
            pieceWidth.toFloat(),
            (offsetY + (pieceHeight - offsetY) / six).toFloat(),
            pieceWidth.toFloat(),
            (offsetY + pieceHeight / six * five).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            (offsetY + pieceHeight / three * two).toFloat()
        )
        path.quadTo(
            (offsetX + pieceWidth + pieceWidth / ten * two).toFloat(),
            (offsetY + pieceHeight - pieceHeight / two).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            (offsetY + pieceHeight).toFloat()
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

    override fun drawRightSideOfPiece(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
    ) {
        path.lineTo(
            (offsetX + pieceWidth).toFloat(),
            (offsetY + pieceHeight).toFloat()
        )
    }

    override fun createTopSideOfPiece(path: Path, xCoord: Int, pieceWidth: Int, yCoord: Int) {
        path.lineTo(xCoord.toFloat() + pieceWidth, yCoord.toFloat())
    }

    override fun drawTopSideOfPiece(path: Path, offsetX: Int, pieceWidth: Int, offsetY: Int) {
        path.lineTo((offsetX + pieceWidth).toFloat(), offsetY.toFloat())
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

    override fun drawTopBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (offsetX + pieceWidth / two).toFloat(),
            (offsetY + pieceHeight / ten * two).toFloat(),
            (offsetX + pieceWidth / three).toFloat(),
            offsetY.toFloat()
        )
        path.cubicTo(
            (offsetX + (pieceWidth - offsetX) / six).toFloat(),
            (offsetY - bumpSize).toFloat(),
            ((offsetX + pieceWidth / six * five)).toFloat(),
            (offsetY - bumpSize).toFloat(),
            (offsetX + pieceWidth / three * two).toFloat(),
            offsetY.toFloat()
        )
        path.quadTo(
            (offsetX + pieceWidth - pieceWidth / two).toFloat(),
            (offsetY + pieceHeight / ten * two).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            offsetY.toFloat()
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

    override fun drawTopCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    ) {
        path.quadTo(
            (offsetX + pieceWidth / two).toFloat(),
            (offsetY - pieceHeight / ten * two).toFloat(),
            (offsetX + pieceWidth / three).toFloat(),
            offsetY.toFloat()
        )
        path.cubicTo(
            (offsetX + (pieceWidth - offsetX) / six).toFloat(),
            (offsetY + bumpSize).toFloat(),
            (offsetX + offsetX + (pieceWidth - offsetX) / six * five).toFloat(),
            (offsetY + bumpSize).toFloat(),
            (offsetX + pieceWidth / three * two).toFloat(),
            offsetY.toFloat()
        )
        path.quadTo(
            (offsetX + pieceWidth / two).toFloat(),
            (offsetY - pieceHeight / ten * two).toFloat(),
            (offsetX + pieceWidth).toFloat(),
            offsetY.toFloat()
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

    override fun drawLeftSideOfPiece(path: Path) {
        path.close()
    }

    companion object {
        const val two = 2
        const val three = 3
        const val five = 5
        const val six = 6
        const val ten = 10

    }
}