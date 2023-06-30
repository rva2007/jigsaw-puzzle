package com.example.jigsawpuzzles

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null
    private var matrix = Matrix()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getImagesFromAssets()
    }

    private fun getImagesFromAssets() {
        val assetManager = assets

        try {
            val files = assetManager.list("img")
            val gridView = findViewById<GridView>(R.id.idGRV)

            gridView.adapter = GridViewAdapter(this@MainActivity)
            gridView.onItemClickListener = AdapterView
                .OnItemClickListener { adapterView, view, i, l ->
                    bitmap = assetsBitmap("img/" + (files!![i % files.size]).toString())
                    resizeBitmapAndRotateIfBitmmapLandscape(bitmap!!)
                        val intent = Intent(applicationContext, SettingsActivity::class.java)
                        intent.putExtra("assetName", bitmap)
                        startActivity(intent)
                        bitmap?.recycle()
                        finish()
                }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun assetsBitmap(str: String): Bitmap? {
        val inputStream: InputStream
        var bitmap: Bitmap? = null
        try {
            inputStream = applicationContext.assets.open(str)
            bitmap = BitmapFactory.decodeStream(inputStream)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        return bitmap
    }

    private fun resizeBitmapAndRotateIfBitmmapLandscape(bmp: Bitmap): Bitmap {
        this.bitmap = bmp
        if (bitmap!!.width > bitmap!!.height) {
            matrix.postRotate(90f)
            bitmap =
                Bitmap.createBitmap(
                    bitmap!!, 0, 0,
                    bitmap!!.width, bitmap!!.height,
                    matrix, true
                )
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 400)
        } else {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 400)
        }
        return bitmap!!
    }
}
