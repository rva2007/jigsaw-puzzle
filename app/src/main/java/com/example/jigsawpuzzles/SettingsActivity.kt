package com.example.jigsawpuzzles

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.jigsawpuzzles.databinding.ActivitySettingsBinding
import com.example.jigsawpuzzles.extentions.PuzzlePathView
import com.squareup.picasso.Picasso
import java.io.File
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

        val metrics: DisplayMetrics = this.getResources().getDisplayMetrics()
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
            val stringGallery = intent.getStringExtra("gallery")
            if (stringGallery != null) {
                val uriFromGallery = Uri.parse(stringGallery)
                Picasso.get()
                    .load(uriFromGallery)
                    .fit()
                    .into(binding.settingsImageView)
            }

            val stringCamera = intent.getStringExtra("camera")
            if (stringCamera != null) {
                val uriFromCamera = Uri.parse(stringCamera)
                Picasso.get()
                    .load(uriFromCamera)
                    .fit()
                    .into(binding.settingsImageView)
            }

            val stringAssets = intent.getStringExtra("assets")
            if (stringAssets != null) {
                val imageUri = Uri.fromFile(File("//android_asset/" + stringAssets))
                Picasso.get()
                    .load(imageUri)
                    .fit()
                    .into(binding.settingsImageView)
            }
        }
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
        stopMediaPlayer()
    }

    private fun stopMediaPlayer() {
        if (MediaPlayer().isPlaying) MediaPlayer().stop()
    }

    private var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener = object :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            playClickSound()
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

    fun checkGameOver() {
        if (isGameOver().not()) return
        playSuccessSound()
        AlertDialogDemonstrator(this).showSuccessAlertDialog()
    }

    private fun onButtonContinueClick() {
        playClickSound()
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
        playClickSound()

        for (piece in pieces!!) {
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (piece.canMove) {
                if (isScreenOrientationPortrait
                    && (lParams.topMargin != binding.containerLayout.height - piece.pieceHeight)
                ) {
                    //this is the place for the animation code


                    randomizePiecePositionOnBottomOfScreen(lParams, piece)
                } else if (isScreenOrientationPortrait.not()
                    && lParams.leftMargin != binding.containerLayout.width - piece.pieceWidth
                ) {
                    //this is the place for the animation code


                    randomizePiecePositionOnRightOfScreen(lParams, piece)
                }
            }
        }
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

    private fun playClickSound() = MediaPlayer.create(this, R.raw.click_sound).start()

    private fun playFitSound() = MediaPlayer.create(this, R.raw.fit_sound).start()

    private fun playSuccessSound() = MediaPlayer.create(this, R.raw.success_sound).start()

    override fun onBackPressed() {
        AlertDialogDemonstrator(this).showConfirmationAlertDialog()
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
                    playFitSound()
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