package com.example.jigsawpuzzles

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.jigsawpuzzles.databinding.ActivitySettingsBinding
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.properties.Delegates
import kotlin.random.Random


class SettingsActivity : AppCompatActivity(), OnTouchListener {
    private lateinit var binding: ActivitySettingsBinding
    private var isScreenOrientationPortrait by Delegates.notNull<Boolean>()
    private var pieces: ArrayList<PuzzlePiece>? = null
    private var imageViewWidth: Int? = null
    private var imageViewHeight: Int? = null
    private var targetWidth: Int? = null
    private var targetHeight: Int? = null
    private var textComplexity: String? = null
    private var columns: Int? = null
    private var rows: Int? = null
    private var complexity: Int? = null
    private var xDelta = 0f
    private var yDelta = 0f

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val orientation = intent.getStringExtra("orientation")
        if (orientation != null) getScreenOrientation(orientation)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideActionBar()

        val metrics: DisplayMetrics = this.resources.displayMetrics
        val screenWidth: Int = metrics.widthPixels
        val screenHeight: Int = metrics.heightPixels

        isScreenOrientationPortrait = ResourcesUtils(this).isScreenOrientationPortrait()

        if (isScreenOrientationPortrait) {
            getPortraitTargetDimensions(screenWidth)
        } else {
            getLandscapeTargetDimensions(screenHeight)
        }

        binding.battonBackPuzzle.isVisible = false
        binding.battonBackPuzzle.setOnClickListener {
            onButtonBackPuzzleClick()
        }
        binding.puzzlePathView.bringToFront()

        binding.seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)

        binding.buttonContinue.setOnClickListener {
            onButtonContinueClick()
        }

        columns = binding.seekBar.progress

        rows = columns!! * bigSideOfImageView / smallSideOfImageView
        binding.puzzlePathView.num = columns!!
        complexity = columns!! * rows!!
        textComplexity = getString(R.string.complexity_text) + " $complexity"
        binding.textViewComplexity.text = textComplexity

        val params = binding.settingsImageView.layoutParams
        params?.width = targetWidth
        params?.height = targetHeight
        binding.settingsImageView.layoutParams = params

        imageViewWidth = binding.settingsImageView.layoutParams?.width
        imageViewHeight = binding.settingsImageView.layoutParams?.height

        binding.puzzlePathView.layoutParams = params

        binding.settingsImageView.post {
            val imageFromCamera = intent.getStringExtra("camera")
            if (imageFromCamera != null) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageFromCamera)
                    .into(binding.settingsImageView)
            }


            val imageFromGallery = intent.getStringExtra("gallery")
            if (imageFromGallery != null) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageFromGallery)
                    .into(binding.settingsImageView)
            }

            val imageFromAssets = intent.getStringExtra("assets")
            if (imageFromAssets != null) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load("file:///android_asset/$imageFromAssets")
                    .into(binding.settingsImageView)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialogDemonstrator(this@SettingsActivity).showConfirmationAlertDialog()
            }

        })
    }

    private fun getLandscapeTargetDimensions(screenHeight: Int) {
        //targetHeight is 80% from screenHeight
        targetHeight = screenHeight - ((screenHeight / oneHundredPercent) * twentyPercent)
        targetWidth = (targetHeight!! / smallSideOfImageView) * bigSideOfImageView
    }

    private fun getPortraitTargetDimensions(screenWidth: Int) {
        //targetWidth is 80% from screenWidth
        targetWidth = screenWidth - ((screenWidth / oneHundredPercent) * twentyPercent)
        targetHeight = (targetWidth!! / smallSideOfImageView) * bigSideOfImageView
    }

    private fun getScreenOrientation(orientation: String) {
        if (orientation.equals("landscape")) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (orientation.equals("portrait")) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
       GameSounds(this).stopMediaPlayer()
    }


    private var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener = object :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            GameSounds(this@SettingsActivity).playClickSound()
            columns = seekBar.progress
            rows = (columns!! * bigSideOfImageView) / smallSideOfImageView
            complexity = columns!! * rows!!
            textComplexity = getString(R.string.complexity_text) + " $complexity"
            binding.textViewComplexity.text = textComplexity
            binding.puzzlePathView.num = columns!!
            binding.puzzlePathView.invalidate()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private fun isGameOver(): Boolean {
        for (piece in pieces!!) {
            if (piece.canMove) {
                return false
            }
        }
        return true
    }

    private fun checkGameOver() {
        if (isGameOver().not()) return
        GameSounds(this@SettingsActivity).playSuccessSound()
        AlertDialogDemonstrator(this).showSuccessAlertDialog()
    }

    private fun onButtonContinueClick() {
        GameSounds(this@SettingsActivity).playClickSound()
        binding.settingsImageView.bringToFront()
        binding.settingsImageView.alpha = 0.3f
        binding.containerLayout.bringToFront()
        binding.puzzlePathView.isGone = true
        binding.textViewComplexity.isVisible = false
        binding.seekBar.isVisible = false
        binding.buttonContinue.isVisible = false
        binding.battonBackPuzzle.isVisible = true

        pieces = ImageSplitter(this).getListOfPuzzles(
            binding.settingsImageView,
            columns!!,
            bigSideOfImageView,
            smallSideOfImageView,
            isScreenOrientationPortrait
        )

        shufflePieces()

        for (piece in pieces!!) {
            piece.setOnTouchListener(this)
            binding.containerLayout.addView(piece)
            randomizePiecePosition(piece)
        }
    }

    private fun shufflePieces() {
        pieces?.shuffle()
    }

    private fun randomizePiecePosition(piece: PuzzlePiece) {
        val lParams = piece.layoutParams as RelativeLayout.LayoutParams
        if (isScreenOrientationPortrait) randomizePiecePositionOnBottomOfScreen(lParams, piece)
        else randomizePiecePositionOnRightOfScreen(lParams, piece)
    }

    private fun onButtonBackPuzzleClick() {
        GameSounds(this@SettingsActivity).playClickSound()

        for (piece in pieces!!) {
            if (piece.canMove) {
                if (isScreenOrientationPortrait && isPieceNotInLowermostPlace(piece)) {
                    AnimationReturnOfPieces(this, piece, binding.containerLayout)
                        .showAnimationForOrientationPortrait()
                    continue
                }
                if (isScreenOrientationPortrait.not() && isPieceNotInRightmostPlace(piece)) {
                    AnimationReturnOfPieces(this, piece, binding.containerLayout)
                        .showAnimationForOrientationLandscape()
                    continue
                }
            }
        }
    }

    private fun isPieceNotInRightmostPlace(piece: PuzzlePiece): Boolean {
        val lParams = piece.layoutParams as RelativeLayout.LayoutParams
        return lParams.leftMargin != binding.containerLayout.width - piece.pieceWidth
    }

    private fun isPieceNotInLowermostPlace(piece: PuzzlePiece): Boolean {
        val lParams = piece.layoutParams as RelativeLayout.LayoutParams
        return lParams.topMargin != binding.containerLayout.height - piece.pieceHeight
    }


    private fun randomizePiecePositionOnRightOfScreen(
        lParams: RelativeLayout.LayoutParams,
        piece: PuzzlePiece
    ) {
        lParams.topMargin = Random.nextInt(binding.containerLayout.height - piece.pieceHeight)
        lParams.leftMargin = binding.containerLayout.width - piece.pieceWidth
        piece.layoutParams = lParams
    }

    private fun randomizePiecePositionOnBottomOfScreen(
        lParams: RelativeLayout.LayoutParams,
        piece: PuzzlePiece
    ) {
        lParams.leftMargin = Random.nextInt(binding.containerLayout.width - piece.pieceWidth)
        lParams.topMargin = binding.containerLayout.height - piece.pieceHeight
        piece.layoutParams = lParams
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val x = motionEvent!!.rawX
        val y = motionEvent.rawY
        val tolerance = getPermissibleDeviationOfCoordinates(view)
        val piece = view as PuzzlePiece
        val lParams = view.layoutParams as RelativeLayout.LayoutParams

        if (piece.canMove.not()) return true

        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xDelta = x - lParams.leftMargin
                yDelta = y - lParams.topMargin
                piece.bringToFront()
            }
            MotionEvent.ACTION_MOVE -> {
                lParams.leftMargin = (x - xDelta).toInt()
                lParams.topMargin = (y - yDelta).toInt()
                view.layoutParams = lParams
            }
            MotionEvent.ACTION_UP -> {
                val xDiff = StrictMath.abs(piece.xCoord - lParams.leftMargin)
                val yDiff = StrictMath.abs(piece.yCoord - lParams.topMargin)

                if (isPieceCloseEnoughToItsPlace(tolerance, xDiff, yDiff)) {
                    GameSounds(this@SettingsActivity).playFitSound()
                    setPieceInItsPlace(lParams, piece)
                    sendPieceToBack(piece)
                    checkGameOver()
                }
            }
        }
        return true
    }

    private fun isPieceCloseEnoughToItsPlace(
        tolerance: Double,
        xDiff: Int,
        yDiff: Int
    ) = xDiff <= tolerance && yDiff <= tolerance

    private fun getPermissibleDeviationOfCoordinates(view: View?) =
        sqrt(
            view!!.width.toDouble().pow(2.0) + view.height.toDouble().pow(2.0)
        ) / settingUpTolerance

    private fun setPieceInItsPlace(
        lParams: RelativeLayout.LayoutParams,
        piece: PuzzlePiece
    ) {
        lParams.leftMargin = piece.xCoord
        lParams.topMargin = piece.yCoord
        piece.layoutParams = lParams
        piece.canMove = false
    }

    private fun sendPieceToBack(child: View) {
        val parent = child.parent as ViewGroup
        parent.removeView(child)
        parent.addView(child, 0)
    }

    companion object {
        const val bigSideOfImageView = 4
        const val smallSideOfImageView = 3
        const val oneHundredPercent = 100
        const val twentyPercent = 20
        const val settingUpTolerance = 10
    }


}