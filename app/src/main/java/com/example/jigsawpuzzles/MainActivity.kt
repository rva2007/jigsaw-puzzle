package com.example.jigsawpuzzles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    val source = ImageDecoder.createSource(this.contentResolver, uri)
                    bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.setTargetSampleSize(1) // shrinking by
                        decoder.isMutableRequired =
                            true // this resolve the hardware type of bitmap problem
                    }
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                    intent.putExtra("camera", uri.toString())
                    startActivity(intent)
                    bitmap!!.recycle()
                    finish()
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {




                val source = ImageDecoder.createSource(this.contentResolver, it)
                bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.setTargetSampleSize(1) // shrinking by
                    decoder.isMutableRequired =
                        true // this resolve the hardware type of bitmap problem
                }




                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                intent.putExtra("gallery", uri.toString())
                startActivity(intent)
                bitmap!!.recycle()
                finish()
            }
        }

    private var latestTmpUri: Uri? = null

    private fun getRotationInDegrees(uri: Uri): Int {
        return ImageOrientationUtil()
            .getExifRotation(
                ImageOrientationUtil()
                    .getFromMediaUri(
                        this,
                        contentResolver,
                        uri
                    )
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()

        getImagesFromAssets()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                AlertDialogDemonstrator(this@MainActivity).showConfirmationAlertDialog()
            }

        })
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
        GameSounds(this).stopMediaPlayer()
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
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun playClickSound() = MediaPlayer.create(this, R.raw.click_sound).start()

    private fun getOrientationScreen(bitmap: Bitmap): String {
        return if (bitmap.width > bitmap.height) "landscape" else "portrait"
    }

    private fun getAssetsBitmap(str: String): Bitmap? {
        val inputStream: InputStream
        var bitmap: Bitmap? = null
        try {
            inputStream = applicationContext.assets.open(str)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        return bitmap
    }

    private fun askForPermissions(): Boolean {
        if (isPermissionsAllowed().not()) {
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
        grantResults: IntArray,
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
                ) != PackageManager.PERMISSION_GRANTED).not()
    }

    companion object {
        const val REQUEST_CODE = 3
    }

}