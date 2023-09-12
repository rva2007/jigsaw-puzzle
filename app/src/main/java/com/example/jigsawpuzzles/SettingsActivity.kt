package com.example.jigsawpuzzles

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.jigsawpuzzles.databinding.ActivitySettingsBinding
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.random.Random


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
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
        getScreenOrientation(orientation!!)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideActionBar()

        //here get dimensions device screen
        val metrics: DisplayMetrics = this.getResources().getDisplayMetrics()
        val screenWidth: Int = metrics.widthPixels
        val screenHeight: Int = metrics.heightPixels

        //here get target dimensions
        if (isScreenOrientationPortrait()) {
            //targetWidth is 80% from screenWidth
            targetWidth = screenWidth - ((screenWidth / 100) * 20)
            targetHeight = (targetWidth!! / smallSideOfImageView) * bigSideOfImageView
        } else {
            //targetHeight is 80% from screenHeight
            targetHeight = screenHeight - ((screenHeight / 100) * 20)
            targetWidth = (targetHeight!! / smallSideOfImageView) * bigSideOfImageView
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
                if (isScreenOrientationPortrait()) {
                    Picasso.get()
                        .load(uriFromGallery)
                        .fit()
                        .into(binding.settingsImageView)
                } else {
                    Picasso.get()
                        .load(uriFromGallery)
//                        .rotate(90f)
                        .fit()
                        .into(binding.settingsImageView)
                }

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
        if (MediaPlayer().isPlaying) {
            MediaPlayer().stop()
        }
    }


    private fun isScreenOrientationPortrait(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }

    private var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener = object :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(
            seekBar: SeekBar, progress: Int,
            fromUser: Boolean
        ) {
            playSoundClick()
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

    private val isGameOver: Boolean
        get() {
            for (piece in pieces!!) {
                if (piece.canMove) {
                    return false
                }
            }
            return true
        }


    fun checkGameOver() {
        if (isGameOver.not()) return
        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
        playSuccessSound()

        AlertDialog.Builder(this@SettingsActivity)
            .setTitle(getString(R.string.you_won_title))
            .setIcon(R.drawable.ic_celebration)
            .setMessage(getString(R.string.you_won_message) + "\n" + getString(R.string.you_won_question))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.confirmation))
                    setIcon(R.drawable.ic_warning_24)
                    setMessage(getString(R.string.are_you_sure))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        onBackPressedDispatcher.onBackPressed()
                    }
                    setNegativeButton(getString(R.string.no)) { _, _ ->
                        startActivity(intent)
                        finish()
                    }
                    setCancelable(false)
                }.create().show()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun playSuccessSound() {
        val successSound = MediaPlayer.create(this@SettingsActivity, R.raw.success_sound)
        successSound.start()
    }


    private fun onButtonContinueClick() {
        playSoundClick()
        binding.settingsImageView.bringToFront()
        binding.settingsImageView.alpha = 0.3f
        binding.containerLayout.bringToFront()
        binding.puzzlePathView.isGone = true
        binding.textViewComplexity.isVisible = false
        binding.seekBar.isVisible = false
        binding.buttonContinue.isVisible = false
        binding.battonBackPuzzle.isVisible = true

        //get list of puzzles
        pieces = ImageSplitter(this@SettingsActivity).onImageSplit(
            binding.settingsImageView,
            columns!!,
            bigSideOfImageView,
            smallSideOfImageView,
            isScreenOrientationPortrait()
        )

        val touchListener = TouchListener(this@SettingsActivity)

        //shuffle pieces order
        pieces?.shuffle()

        for (piece in pieces!!) {
            piece.setOnTouchListener(touchListener)
            binding.containerLayout.addView(piece)
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (isScreenOrientationPortrait()) {
                //randomize position on the bottom of screen
                lParams.leftMargin = Random.nextInt(
                    binding.containerLayout.width - piece.pieceWidth
                )
                lParams.topMargin = binding.containerLayout.height - piece.pieceHeight
                piece.layoutParams = lParams
            } else {
                //randomize position on the right of screen
                lParams.topMargin = Random.nextInt(
                    binding.containerLayout.height - piece.pieceHeight
                )
                lParams.leftMargin = binding.containerLayout.width - piece.pieceWidth
                piece.layoutParams = lParams
            }
        }
    }

    private fun onButtonBackPuzzleClick() {
        playSoundClick()

        for (piece in pieces!!) {
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (piece.canMove) {
                if (isScreenOrientationPortrait()
                    && (lParams.topMargin != binding.containerLayout.height - piece.pieceHeight)
                ) {
                    //this is the place for the animation code


                    //randomize position on the bottom of screen
                    lParams.leftMargin =
                        Random.nextInt(binding.containerLayout.width - piece.pieceWidth)
                    lParams.topMargin =
                        binding.containerLayout.height - piece.pieceHeight
                    piece.layoutParams = lParams
                } else if (isScreenOrientationPortrait().not()
                    && lParams.leftMargin != binding.containerLayout.width - piece.pieceWidth
                ) {
                    //this is the place for the animation code


                    //randomize position on the right of screen
                    lParams.topMargin =
                        Random.nextInt(binding.containerLayout.height - piece.pieceHeight)
                    lParams.leftMargin =
                        binding.containerLayout.width - piece.pieceWidth
                    piece.layoutParams = lParams
                }
            }
        }
    }


    private fun playSoundClick() {
        val clickSound = MediaPlayer.create(this@SettingsActivity, R.raw.click_sound)
        clickSound.start()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.confirmation))
            setIcon(R.drawable.ic_warning_24)
            setMessage(getString(R.string.are_you_sure))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                onBackPressedDispatcher.onBackPressed()
            }
            setNegativeButton(getString(R.string.no)) { _, _ ->
            }
            setCancelable(false)
        }.create().show()
    }

}