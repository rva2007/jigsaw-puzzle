package com.example.jigsawpuzzles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaPlayer
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
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.example.jigsawpuzzles.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var bitmap: Bitmap? = null
    private var matrix = Matrix()

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    intent.putExtra("camera", uri.toString())

                    startActivity(intent)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val path = it.path
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), it)
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                intent.putExtra("gallery", uri.toString())
                startActivity(intent)
            }
        }

    private var latestTmpUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()

        getImagesFromAssets()
    }

    private fun setClickListeners() {
        binding.buttonCamera.setOnClickListener { takeImage() }
        binding.buttonGallery.setOnClickListener { selectImageFromGallery() }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")


    private fun takeImage() {
        lifecycleScope.launch {
            // Suspend until you are STARTED
            withStarted { }
            // Run your code that happens after you become STARTED here
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
            // Note: your code will continue to run even if the Lifecycle falls below STARTED
        }

    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
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
            binding.gridView.adapter = GridViewAdapter(this@MainActivity)
            binding.gridView.onItemClickListener = AdapterView
                .OnItemClickListener { _, _, i, _ ->
                    playClickSound()
                    val path = "img/" + (files!![i % files.size]).toString()
                    val uri = Uri.parse(path)
                    bitmap = getAssetsBitmap(path)
                    val intent = Intent(applicationContext, SettingsActivity::class.java)
                    intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                    intent.putExtra("assets", uri.toString())
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
        val bWidth = bitmap.width
        val bHeight = bitmap.height
        if (bWidth > bHeight) {
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
                if ((grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) return
                else askForPermissions()
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

    companion object {
        const val REQUEST_CODE = 3
    }

}
