package com.example.jigsawpuzzles

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
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
                if (piece.dataOfPiece.canMove) {
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

    private fun onImageSplit(imageView: ImageView?, number: Int): ArrayList<PuzzlePiece> {
        val columns: Int
        val rows: Int

        if (isScreenOrientationPortrait()) {
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
                val piece = PuzzlePiece(applicationContext)
                piece.setImageBitmap(pieceBitmap)
                piece.dataOfPiece.xCoord = xCoord - offsetX + imageView.left
                piece.dataOfPiece.yCoord = yCoord - offsetY + imageView.top
                piece.dataOfPiece.pieceWidth = pieceWidth + offsetX
                piece.dataOfPiece.pieceHeight = pieceHeight + offsetY
                //this bitmap will hold our final puzzle piece image
                val puzzlePiece = Bitmap.createBitmap(
                    pieceWidth + offsetX, pieceHeight + offsetY, Bitmap.Config.ARGB_8888
                )
                //draw path
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
                val paint = Paint()
                paint.color = -0x10000000
                paint.style = Paint.Style.FILL
                canvas.drawPath(path, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(pieceBitmap, 0f, 0f, paint)

                //draw a white border
                var border = Paint()
                border.color = -0x7f000001
                border.style = Paint.Style.STROKE
                border.strokeWidth = 8.0f
                canvas.drawPath(path, border)

                //draw a black border
                border = Paint()
                border.color = -0x80000000
                border.style = Paint.Style.STROKE
                border.strokeWidth = 3.0f
                canvas.drawPath(path, border)

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
        pieces = onImageSplit(binding.settingsImageView, columns!!)

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
                    binding.containerLayout.width - piece.dataOfPiece.pieceWidth
                )
                lParams.topMargin = binding.containerLayout.height - piece.dataOfPiece.pieceHeight
                piece.layoutParams = lParams
            } else {
                //randomize position on the right of screen
                lParams.topMargin = Random.nextInt(
                    binding.containerLayout.height - piece.dataOfPiece.pieceHeight
                )
                lParams.leftMargin = binding.containerLayout.width - piece.dataOfPiece.pieceWidth
                piece.layoutParams = lParams
            }
        }
    }

    private fun onButtonBackPuzzleClick() {
        playSoundClick()

        for (piece in pieces!!) {
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (piece.dataOfPiece.canMove) {
                if (isScreenOrientationPortrait()
                    && (lParams.topMargin != binding.containerLayout.height - piece.dataOfPiece.pieceHeight)
                ) {
                    //this is the place for the animation code


                    //randomize position on the bottom of screen
                    lParams.leftMargin =
                        Random.nextInt(binding.containerLayout.width - piece.dataOfPiece.pieceWidth)
                    lParams.topMargin =
                        binding.containerLayout.height - piece.dataOfPiece.pieceHeight
                    piece.layoutParams = lParams
                } else if (!isScreenOrientationPortrait()
                    && lParams.leftMargin != binding.containerLayout.width - piece.dataOfPiece.pieceWidth
                ) {
                    //this is the place for the animation code


                    //randomize position on the right of screen
                    lParams.topMargin =
                        Random.nextInt(binding.containerLayout.height - piece.dataOfPiece.pieceHeight)
                    lParams.leftMargin =
                        binding.containerLayout.width - piece.dataOfPiece.pieceWidth
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