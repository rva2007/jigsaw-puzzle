package com.example.jigsawpuzzles

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.jigsawpuzzles.databinding.ActivitySettingsBinding
import com.example.jigsawpuzzles.extentions.PuzzlePathView
import kotlinx.coroutines.delay
import kotlin.random.Random

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    private var pieces: ArrayList<PuzzlePiece>? = null
    private var isPiecesShow: Boolean = false
    private var containerLayout: RelativeLayout? = null
    private var imageView: ImageView? = null
    private var puzzlePathView: ImageView? = null
    private var backPuzzle: ImageView? = null
    private var tvComplexity: TextView? = null
    private var seekBar: SeekBar? = null
    private var buttonContinue: Button? = null
    private var bitmapFromAssets: Bitmap? = null
    private var bitmapFromGallery: Bitmap? = null
    private var bitmapFromCamera: Bitmap? = null
    private var bitmap: Bitmap? = null
    private var imageViewWidth: Int? = null
    private var imageViewHeight: Int? = null
    private var targetWidth: Int? = null
    private var targetHeight: Int? = null
    private var str: String? = null
    private var columns: Int? = null
    private var rows: Int? = null
    private var complexity: Int? = null
    val bigSideOfImageView = 4
    val smallSideOfImageView = 3

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val orientation = intent.getStringExtra("orientation")
        if (orientation.equals("landscape")) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (orientation.equals("portrait")) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //here hide the action bar
        supportActionBar?.hide()

        //get Display from the WindowManager
        val display: Display? = baseContext.display
        val point = Point()
        display?.getSize(point)

        windowManager.currentWindowMetricsPointCompat()

        //here get dimensions device screen
        val screenWidth: Int = point.x
        val screenHeight: Int = point.y
        //here get target dimensions
        if (screenOrientationIsPortrait()) {
            //targetWidth is 80% from screenWidth
            targetWidth = screenWidth - ((screenWidth / 100) * 20)
            targetHeight = (targetWidth!! / smallSideOfImageView) * bigSideOfImageView
        } else {
            //targetHeight is 80% from screenHeight
            targetHeight = screenHeight - ((screenHeight / 100) * 20)
            targetWidth = (targetHeight!! / smallSideOfImageView) * bigSideOfImageView
        }

        imageView = binding.settingsImageView
        puzzlePathView = binding.puzzlePathView
        backPuzzle = binding.backPuzzle
        backPuzzle?.isVisible = false
        puzzlePathView?.bringToFront()
        containerLayout = binding.containerLayout
        tvComplexity = binding.tvComplexity
        buttonContinue = binding.btnContinue
        seekBar = binding.seekBar

        seekBar?.setOnSeekBarChangeListener(onSeekBarChangeListener)
        columns = seekBar!!.progress

        rows = columns!! * bigSideOfImageView / smallSideOfImageView
        (puzzlePathView as PuzzlePathView).num = columns!!
        complexity = columns!! * rows!!
        str = getString(R.string.complexity_text) + " $complexity"
        tvComplexity!!.text = str

        val params = imageView?.layoutParams
        params?.width = targetWidth
        params?.height = targetHeight
        imageView?.layoutParams = params

        imageViewWidth = imageView?.layoutParams?.width
        imageViewHeight = imageView?.layoutParams?.height

        puzzlePathView?.layoutParams = params

        bitmapFromAssets = intent.getParcelableExtra("assets")
        bitmapFromGallery = intent.getParcelableExtra("gallery")
        bitmapFromCamera = intent.getParcelableExtra("camera")

        if (bitmapFromAssets != null) {
            bitmap = bitmapFromAssets
        }
        if (bitmapFromGallery != null) {
            bitmap = bitmapFromGallery
        }
        if (bitmapFromCamera != null) {
            bitmap = bitmapFromCamera
        }

        imageView?.post {
            if (!screenOrientationIsPortrait()) {
                val matrix = Matrix()
                matrix.postRotate(-90f)
                bitmap = Bitmap.createBitmap(
                    bitmap!!, 0, 0, bitmap!!.width, bitmap!!.height, matrix, true
                )
            }
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, imageViewWidth!!, imageViewHeight!!)
            imageView?.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MediaPlayer().isPlaying) {
            MediaPlayer().stop()
        }
    }

    private fun screenOrientationIsPortrait(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }

    private fun WindowManager.currentWindowMetricsPointCompat(): Point {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsets = currentWindowMetrics.windowInsets
            var insets: Insets = windowInsets.getInsets(WindowInsets.Type.navigationBars())
            windowInsets.displayCutout?.run {
                insets = Insets.max(
                    insets,
                    Insets.of(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom)
                )
            }
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom
            Point(
                currentWindowMetrics.bounds.width() - insetsWidth,
                currentWindowMetrics.bounds.height() - insetsHeight
            )
        } else {
            Point().apply {
                defaultDisplay.getSize(this)
            }
        }
    }

    private var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener = object :
        SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(
            seekBar: SeekBar, progress: Int,
            fromUser: Boolean
        ) {
            clickSound()
            columns = seekBar.progress
            rows = (columns!! * bigSideOfImageView) / smallSideOfImageView
            complexity = columns!! * rows!!
            str = getString(R.string.complexity_text) + " $complexity"
            binding.tvComplexity.text = str
            binding.puzzlePathView.num = columns!!
            binding.puzzlePathView.invalidate()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private val isGameOver: Boolean
        get() {
            for (piece in pieces!!) {
                if (piece.pieceData.canMove) {
                    return false
                }
            }
            return true
        }


    fun checkGameOver() {
        if (isGameOver) {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            successSound()

            AlertDialog.Builder(this@SettingsActivity)
                .setTitle(getString(R.string.you_won_title))
                .setIcon(R.drawable.ic_celebration)
                .setMessage(getString(R.string.you_won_message) + "\n" + getString(R.string.you_won_question))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.confirmation))
                        setIcon(R.drawable.ic_warning_24)
                        setMessage(getString(R.string.are_you_sure))
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            super.onBackPressed()
                        }
                        setNegativeButton(getString(R.string.no)) { _, _ ->
                            startActivity(intent)
                            dialog.dismiss()
                            finish()
                        }
                        setCancelable(true)
                    }.create().show()
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun successSound() {
        val successSound = MediaPlayer.create(this@SettingsActivity, R.raw.success_sound)
        successSound.start()
    }

    private fun splitImage(imageView: ImageView?, number: Int): ArrayList<PuzzlePiece> {
        val columns: Int
        val rows: Int

        if (screenOrientationIsPortrait()) {
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
                piece.pieceData.xCoord = xCoord - offsetX + imageView.left
                piece.pieceData.yCoord = yCoord - offsetY + imageView.top
                piece.pieceData.pieceWidth = pieceWidth + offsetX
                piece.pieceData.pieceHeight = pieceHeight + offsetY
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
                    //top side piece
                    topSidePiece(path, pieceBitmap, offsetY)
                } else {
                    //top bump
                    topBump(path, offsetX, pieceBitmap, offsetY, bumpSize)
                }
                if (column == columns - 1) {
                    //right side piece
                    rightSidePiece(path, pieceBitmap)
                } else {
                    //right bump
                    rightBump(path, pieceBitmap, offsetY, bumpSize)
                }
                if (row == rows - 1) {
                    //bottom side piece
                    bottomSidePiece(path, offsetX, pieceBitmap)
                } else {
                    //bottom bump
                    bottomBump(path, offsetX, pieceBitmap, bumpSize)
                }
                if (column == 0) {
                    //left side piece
                    path.close()
                } else {
                    //left bump
                    leftBump(path, offsetX, offsetY, pieceBitmap, bumpSize)
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

    private fun leftBump(
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

    private fun bottomBump(
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

    private fun rightBump(
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

    private fun rightSidePiece(path: Path, pieceBitmap: Bitmap) {
        path.lineTo(
            pieceBitmap.width.toFloat(),
            pieceBitmap.height.toFloat()
        )
    }

    private fun bottomSidePiece(
        path: Path,
        offsetX: Int,
        pieceBitmap: Bitmap
    ) {
        path.lineTo(
            offsetX.toFloat(), pieceBitmap.height.toFloat()
        )
    }

    private fun topBump(
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

    private fun topSidePiece(
        path: Path,
        pieceBitmap: Bitmap,
        offsetY: Int
    ) {
        path.lineTo(
            pieceBitmap.width.toFloat(),
            offsetY.toFloat()
        )
    }


    fun onButtonClick(view: View) {
        clickSound()
        imageView?.bringToFront()
        imageView?.alpha = 0.3f
        containerLayout?.bringToFront()
        puzzlePathView?.isGone = true
        tvComplexity?.isVisible = false
        seekBar?.isVisible = false
        buttonContinue?.isVisible = false
        backPuzzle?.isVisible = true

        //get list of puzzles
        pieces = splitImage(imageView!!, columns!!)

        val touchListener = TouchListener(this@SettingsActivity)

        //shuffle pieces order
        pieces?.shuffle()

        for (piece in pieces!!) {
            piece.setOnTouchListener(touchListener)
            containerLayout!!.addView(piece)
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (screenOrientationIsPortrait()) {
                //randomize position on the bottom of screen
                lParams.leftMargin = Random.nextInt(
                    containerLayout!!.width - piece.pieceData.pieceWidth
                )
                lParams.topMargin = containerLayout!!.height - piece.pieceData.pieceHeight
                piece.layoutParams = lParams
            } else {
                //randomize position on the right of screen
                lParams.topMargin = Random.nextInt(
                    containerLayout!!.height - piece.pieceData.pieceHeight
                )
                lParams.leftMargin = containerLayout!!.width - piece.pieceData.pieceWidth
                piece.layoutParams = lParams
            }
        }
    }

    fun onBackPuzzleClick(view: View) {
        clickSound()

        for (piece in pieces!!) {
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (piece.pieceData.canMove) {
                if (screenOrientationIsPortrait()
                    && (lParams.topMargin != containerLayout!!.height - piece.pieceData.pieceHeight)
                ) {
                    //this is the place for the animation code
//                    startRotate(piece)
                    /**
                     * fromX
                     * toX
                     * fromY
                     * toY**/
                    //randomize position on the bottom of screen
                    startTranslate(
                        piece,
                        piece.pieceData.x.toFloat(),
                        lParams.leftMargin.toFloat(),
                        piece.pieceData.y.toFloat(),
                        lParams.topMargin.toFloat(),
                        lParams
                    )


                    lParams.leftMargin =
                        Random.nextInt(containerLayout!!.width - piece.pieceData.pieceWidth)
                    lParams.topMargin = containerLayout!!.height - piece.pieceData.pieceHeight
                    piece.layoutParams = lParams






                    startTranslateXY(piece)


                } else if (!screenOrientationIsPortrait()
                    && lParams.leftMargin != containerLayout!!.width - piece.pieceData.pieceWidth
                ) {
                    //this is the place for the animation code


                    //randomize position on the right of screen
                    lParams.topMargin =
                        Random.nextInt(containerLayout!!.height - piece.pieceData.pieceHeight)
                    lParams.leftMargin = containerLayout!!.width - piece.pieceData.pieceWidth

//                    piece.layoutParams = lParams
                }
            }
        }
    }

    fun startTranslate(tv_target: View,fromX:Float,toX:Float, fromY:Float,toY:Float,lParams: RelativeLayout.LayoutParams) {
        val translateAnimation = TranslateAnimation(fromX, toX, fromY, toY)
        translateAnimation.duration = 500
        tv_target.startAnimation(translateAnimation)
        tv_target.layoutParams = lParams
    }

    fun startRotate(view: View) {
        val rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        rotateAnimator.duration = 1000
        rotateAnimator.start()
    }

    fun startTranslateXY(view: View) {
        val currentX: Float = view.translationX
        val currentY: Float = view.translationY
        val translateXAnimator =
            ObjectAnimator.ofFloat(view, "translationX", currentX, 0f, currentX)
        translateXAnimator.duration = 1000
        translateXAnimator.start()
        val translateYAnimator =
            ObjectAnimator.ofFloat(view, "translationY", currentY, -500f, currentY)
        translateYAnimator.duration = 1000
        translateYAnimator.start()
    }


    private fun clickSound() {
        val clickSound = MediaPlayer.create(this@SettingsActivity, R.raw.click_sound)
        clickSound.start()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.confirmation))
            setIcon(R.drawable.ic_warning_24)
            setMessage(getString(R.string.are_you_sure))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                super.onBackPressed()
            }
            setNegativeButton(getString(R.string.no)) { _, _ ->
            }
            setCancelable(true)
        }.create().show()
    }

}