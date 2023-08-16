package com.example.jigsawpuzzles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.jigsawpuzzles.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var requestCode: Int? = null
    private var bitmap: Bitmap? = null
    private var matrix = Matrix()
    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                val uri = result.data!!.data
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                resizeBitmapAndRotateIfBitmapLandscape(bitmap!!)
                intent.putExtra("gallery", bitmap)
                startActivity(intent)
                bitmap?.recycle()
                finish()
            }
        }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val intent = Intent(applicationContext, SettingsActivity::class.java)
                bitmap = result.data?.extras?.get("data") as Bitmap
                intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                resizeBitmapAndRotateIfBitmapLandscape(bitmap!!)
                intent.putExtra("camera", bitmap)
                startActivity(intent)
                bitmap?.recycle()
                finish()

            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCamera.setOnClickListener {
            onCameraImageClicked()
        }
        binding.buttonGallery.setOnClickListener {
            onGalleryImageClicked()
        }

        getImagesFromAssets()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MediaPlayer().isPlaying) {
            MediaPlayer().stop()
        }
    }

    private fun getImagesFromAssets() {
        val assetManager = assets

        try {
            val files = assetManager.list("img")
            binding.gridView?.adapter = GridViewAdapter(this@MainActivity)
            binding.gridView?.onItemClickListener = AdapterView
                .OnItemClickListener { _, _, i, _ ->
                    playClickSound()
                    bitmap = getAssetsBitmap("img/" + (files!![i % files.size]).toString())
                    val intent = Intent(applicationContext, SettingsActivity::class.java)
                    intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                    resizeBitmapAndRotateIfBitmapLandscape(bitmap!!)
                    intent.putExtra("assets", bitmap)
                    startActivity(intent)
                    bitmap!!.recycle()
                    finish()
                }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun playClickSound() {
        val clickSound = MediaPlayer.create(this@MainActivity, R.raw.click_sound)
        clickSound.start()
    }

    private fun getOrientationScreen(bitmap: Bitmap): String {
        val orientation: String
        if (bitmap.width > bitmap.height) {
            orientation = "landscape"
        } else {
            orientation = "portrait"
        }
        return orientation
    }

    private fun getAssetsBitmap(str: String): Bitmap? {
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

    private fun resizeBitmapAndRotateIfBitmapLandscape(bmp: Bitmap): Bitmap {
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

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.CAMERA
                )
            ) {
                showPermissionDeniedDialog()
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_CODE
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission is denied, you can ask for permission again, if you want
                    askForPermissions()
                }
                return
            }
        }
    }


    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings")
            { dialogInterface, i ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun isPermissionsAllowed(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED)
    }


    fun onCameraImageClicked() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )
        } else {
            playClickSound()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraResultLauncher.launch(intent)
        }
    }


    fun onGalleryImageClicked() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
            )
        } else {
            playClickSound()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            galleryResultLauncher.launch(intent)
        }
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
            setCancelable(false)
        }.create().show()
    }

    companion object {
        const val CAMERA_REQUEST = 1
        const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2
        const val REQUEST_CODE = 3
    }

}
