package com.example.jigsawpuzzles.extentions

import android.graphics.Path

interface InterfacePuzzlePathView {
    fun createLeftBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createLeftCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createBottomCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createBottomBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createBottomSideOfPiece(
        path: Path,
        xCoord: Int,
        yCoord: Int,
        pieceHeight: Int,
    )

    fun createRightBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createRightCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createRightSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
    )

    fun createTopSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
    )

    fun createTopBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createTopCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createLeftSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
    )
}