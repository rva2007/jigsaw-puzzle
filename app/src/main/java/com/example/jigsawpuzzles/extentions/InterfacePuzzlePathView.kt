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

    fun drawLeftBump(
        path: Path,
        pieceWidth: Int,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
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

    fun drawLeftCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
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

    fun drawBottomCave(
        path: Path,
        pieceWidth: Int,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
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

    fun drawBottomBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun createBottomSideOfPiece(
        path: Path,
        xCoord: Int,
        yCoord: Int,
        pieceHeight: Int,
    )

    fun drawBottomSideOfPiece(
        path: Path,
        offsetX: Int,
        pieceHeight: Int,
        offsetY: Int,
    )

    fun createRightBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun drawRightBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
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

    fun drawRightCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
    )

    fun createRightSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
    )

    fun drawRightSideOfPiece(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int
    )

    fun createTopSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
    )

    fun drawTopSideOfPiece(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
    )

    fun createTopBump(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun drawTopBump(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int
    )

    fun createTopCave(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
        bumpSize: Int,
    )

    fun drawTopCave(
        path: Path,
        offsetX: Int,
        pieceWidth: Int,
        offsetY: Int,
        pieceHeight: Int,
        bumpSize: Int
    )

    fun createLeftSideOfPiece(
        path: Path,
        xCoord: Int,
        pieceWidth: Int,
        yCoord: Int,
        pieceHeight: Int,
    )

    fun drawLeftSideOfPiece(path: Path)
}