package com.example.jigsawpuzzles

import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.jigsawpuzzles.databinding.ActivitySettingsBinding
import com.example.jigsawpuzzles.extentions.PuzzlePathView
import kotlin.random.Random

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    private var pieces: ArrayList<PuzzlePiece>? = null
    private var isPiecesShow:Boolean = false
    private var container_layout: RelativeLayout? = null
    private var imageView: ImageView? = null
    private var puzzlePathView: ImageView? = null
    private var backPuzzle: ImageView? = null
    private var tv_complexity: TextView? = null
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        var info = "Ширина экрана: $screenWidth; Высота экрана: $screenHeight"
        Log.i("log", info)


        //here get target dimensions
        if (screenOrientationIsPortrait()) {
            targetWidth = screenWidth - (screenWidth / 100 * 20)
            targetHeight = targetWidth!! / 3 * 4
        } else {
            targetHeight = screenHeight - (screenHeight / 100 * 20)
            targetWidth = targetHeight!! / 3 * 4
        }

        imageView = binding.settingsImageView
        puzzlePathView = binding.puzzlePathView
        backPuzzle = binding.backPuzzle
        backPuzzle?.isVisible = false
        puzzlePathView?.bringToFront()
        container_layout = binding.containerLayout
        tv_complexity = binding.tvComplexity
        buttonContinue = binding.btnContinue
        seekBar = binding.seekBar

        seekBar?.setOnSeekBarChangeListener(onSeekBarChangeListener)
        columns = seekBar!!.progress

        rows = columns!! * 4 / 3
        (puzzlePathView as PuzzlePathView).num = columns!!
        complexity = columns!! * rows!!
        str = getString(R.string.complexity_text) + " $complexity"
        tv_complexity!!.text = str


        val params = imageView?.layoutParams
        params?.width = targetWidth
        params?.height = targetHeight
        imageView?.layoutParams = params

        imageViewWidth = imageView?.layoutParams?.width
        imageViewHeight = imageView?.layoutParams?.height

        puzzlePathView?.layoutParams = params

        info =
            "Ширина imageView: $imageViewWidth; Высота imageView: $imageViewHeight"

        Log.i("log", info)

        val intent = intent
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
            bitmap =
                ThumbnailUtils.extractThumbnail(bitmap, imageViewWidth!!, imageViewHeight!!)
            imageView?.setImageBitmap(bitmap)
        }


    }


    private fun screenOrientationIsPortrait(): Boolean {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }

    fun WindowManager.currentWindowMetricsPointCompat(): Point {
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
            columns = seekBar.progress
            rows = columns!! * 4 / 3
            complexity = columns!! * rows!!
            str =
                getString(R.string.complexity_text) + " $complexity"
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
            AlertDialog.Builder(this@SettingsActivity)
                .setTitle(getString(R.string.you_won_title))
                .setIcon(R.drawable.ic_celebration)
                .setMessage(getString(R.string.you_won_message) + "\n" + getString(R.string.you_won_question))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                    intent.putExtra("exit", "exit")
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
                .create()
                .show()
        }

    }

    private fun splitImage(imageView: ImageView?, number: Int): ArrayList<PuzzlePiece> {
        val columns: Int
        val rows: Int

        if (screenOrientationIsPortrait()) {
            columns = number
            rows = columns * 4 / 3

        } else {
            rows = number
            columns = rows * 4 / 3
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
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        offsetY.toFloat()
                    )
                } else {
                    //top bump
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
                if (column == columns - 1) {
                    //right side piece
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                } else {
                    //right bump
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
                if (row == rows - 1) {
                    //bottom side piece
                    path.lineTo(
                        offsetX.toFloat(), pieceBitmap.height.toFloat()
                    )
                } else {
                    //bottom bump
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
                if (column == 0) {
                    //left side piece
                    path.close()
                } else {
                    //left bump
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


    fun onButtonClick(view: View) {
        imageView?.bringToFront()
        imageView?.alpha = 0.3f
        container_layout?.bringToFront()
        puzzlePathView?.isGone = true
        tv_complexity?.isVisible = false
        seekBar?.isVisible = false
        buttonContinue?.isVisible = false
        backPuzzle?.isVisible = true

        //get list of puzzles
        pieces = splitImage(imageView!!, columns!!)

        val touchListener = TouchListener(this@SettingsActivity)

        if (pieces != null) isPiecesShow = true

        //shuffle pieces order
        pieces?.shuffle()
        for (piece in pieces!!) {
            piece.setOnTouchListener(touchListener)
            container_layout!!.addView(piece)

            val lParams = piece.layoutParams as RelativeLayout.LayoutParams
            if (screenOrientationIsPortrait()) {
                //randomize position on the bottom of screen
                lParams.leftMargin = Random.nextInt(
                    container_layout!!.width - piece.pieceData.pieceWidth
                )
                lParams.topMargin = container_layout!!.height - piece.pieceData.pieceHeight

                piece.layoutParams = lParams

            } else {
                //randomize position on the right of screen
                lParams.topMargin = Random.nextInt(
                    container_layout!!.height - piece.pieceData.pieceHeight
                )
                lParams.leftMargin = container_layout!!.width - piece.pieceData.pieceWidth

                piece.layoutParams = lParams

            }
        }
    }

    fun onBackPuzzleClick(view: View) {
        for (piece in pieces!!) {
            val lParams = piece.layoutParams as RelativeLayout.LayoutParams

            if (piece.pieceData.canMove) {
                if (screenOrientationIsPortrait()
                    && (lParams.topMargin != container_layout!!.height - piece.pieceData.pieceHeight)
                ) {
                    //randomize position on the bottom of screen
                    lParams.leftMargin = Random.nextInt(container_layout!!.width - piece.pieceData.pieceWidth)
                    lParams.topMargin = container_layout!!.height - piece.pieceData.pieceHeight
                    piece.layoutParams = lParams

                } else if (!screenOrientationIsPortrait()
                    && lParams.leftMargin != container_layout!!.width - piece.pieceData.pieceWidth
                ) {
                    //randomize position on the right of screen
                    lParams.topMargin = Random.nextInt(container_layout!!.height - piece.pieceData.pieceHeight)
                    lParams.leftMargin = container_layout!!.width - piece.pieceData.pieceWidth
                    piece.layoutParams = lParams
                }
            }
        }
    }

}
