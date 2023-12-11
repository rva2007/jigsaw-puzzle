package com.example.jigsawpuzzles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.bumptech.glide.Glide
import com.example.jigsawpuzzles.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

// This app used photos by Andrey Pavlov (known on the Internet by the nickname Antrey)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bitmap: Bitmap
    private var latestTmpUri: Uri? = null


    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                runBlocking {
                    launch(Dispatchers.IO) {
                        bitmap = Glide.with(applicationContext)
                            .asBitmap()
                            .load("$uri")
                            .submit()
                            .get()
                    }
                }

                val intent = Intent(applicationContext, SettingsActivity::class.java)
                intent.putExtra("orientation", screenOrientation())
                intent.putExtra("gallery", uri.toString())
                startActivity(intent)
                bitmap.recycle()
                finish()
            }
        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->

                    runBlocking {
                        launch(Dispatchers.IO) {
                            bitmap = Glide.with(applicationContext)
                                .asBitmap()
                                .load("$uri")
                                .submit()
                                .get()
                        }
                    }

                    val intent = Intent(applicationContext, SettingsActivity::class.java)
                    intent.putExtra("orientation", screenOrientation())
                    intent.putExtra("camera", uri.toString())
                    startActivity(intent)
                    bitmap.recycle()
                    finish()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()

        getImagesFromAssets()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialogDemonstrator(this@MainActivity).showConfirmationAlertDialog()
            }

        })
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

    private fun setClickListeners() {
        binding.buttonCamera.setOnClickListener {
            takeImage()
            GameSounds(this@MainActivity).playClickSound()
        }
        binding.buttonGallery.setOnClickListener {
            selectImageFromGallery()
            GameSounds(this@MainActivity).playClickSound()
        }
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
            binding.gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                val path = "img/" + (files!![i % files.size]).toString()
                val uri = Uri.parse(path)

                runBlocking {
                    launch(Dispatchers.IO) {
                        bitmap = Glide.with(applicationContext)
                            .asBitmap()
                            .load("file:///android_asset/$uri")
                            .submit()
                            .get()
                    }
                }

                GameSounds(this@MainActivity).playClickSound()

                val intent = Intent(applicationContext, SettingsActivity::class.java)
                intent.putExtra("orientation", screenOrientation())
                intent.putExtra("assets", uri.toString())
                startActivity(intent)
                bitmap.recycle()
                finish()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun screenOrientation() =
        if (bitmap.width > bitmap.height) "landscape" else "portrait"

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
                ) != PackageManager.PERMISSION_GRANTED).not()
    }

    companion object {
        const val REQUEST_CODE = 3
    }

}