package com.example.jigsawpuzzles

import android.content.pm.ActivityInfo
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.jigsawpuzzles.databinding.ActivitySettingsBinding
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.properties.Delegates
import kotlin.random.Random


class SettingsActivity : AppCompatActivity() {
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
    private val bigSideOfImageView = 4
    private val smallSideOfImageView = 3


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
        targetHeight = screenHeight - ((screenHeight / 100) * 20)
        targetWidth = (targetHeight!! / smallSideOfImageView) * bigSideOfImageView
    }

    private fun getPortraitTargetDimensions(screenWidth: Int) {
        //targetWidth is 80% from screenWidth
        targetWidth = screenWidth - ((screenWidth / 100) * 20)
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
            this@SettingsActivity.playClickSound()
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

    private fun playSuccessSound() = MediaPlayer.create(this, R.raw.success_sound).start()

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

        //get list of puzzles
        pieces = ImageSplitter(this).onImageSplit(
            binding.settingsImageView,
            columns!!,
            bigSideOfImageView,
            smallSideOfImageView,
            isScreenOrientationPortrait
        )

        val touchListener = TouchListener(this)

        //shuffle pieces order
        pieces?.shuffle()

        for (piece in pieces!!) {
            piece.setOnTouchListener(touchListener)
            binding.containerLayout.addView(piece)
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (isScreenOrientationPortrait) {
                randomizePiecePositionOnBottomOfScreen(lParams, piece)
            } else {
                randomizePiecePositionOnRightOfScreen(lParams, piece)
            }
        }
    }

    private fun onButtonBackPuzzleClick() {
        this.playClickSound()

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
        lParams.topMargin =
            Random.nextInt(binding.containerLayout.height - piece.pieceHeight)
        lParams.leftMargin =
            binding.containerLayout.width - piece.pieceWidth
        piece.layoutParams = lParams
    }

    private fun randomizePiecePositionOnBottomOfScreen(
        lParams: RelativeLayout.LayoutParams,
        piece: PuzzlePiece
    ) {
        lParams.leftMargin = Random.nextInt(
            binding.containerLayout.width - piece.pieceWidth
        )
        lParams.topMargin = binding.containerLayout.height - piece.pieceHeight
        piece.layoutParams = lParams
    }

    private fun playClickSound() = MediaPlayer.create(this, R.raw.click_sound).start()

    override fun onBackPressed() {
        AlertDialogDemonstrator(this).showConfirmationAlertDialog()
    }

}