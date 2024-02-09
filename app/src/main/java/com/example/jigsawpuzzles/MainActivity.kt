package com.example.jigsawpuzzles

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.bumptech.glide.Glide
import com.example.jigsawpuzzles.databinding.ActivityMainBinding
import com.example.jigsawpuzzles.extentions.ApplicationDetailSettingsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException


// This app used photos by Andrey Pavlov (known on the Internet by the nickname Antrey)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var bitmap: Bitmap
    private var latestTmpUri: Uri? = null

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {

                runBlocking {
                    launch(Dispatchers.IO) {
                        bitmap =
                            Glide.with(applicationContext).asBitmap().load("$uri").submit().get()
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
                            bitmap = Glide.with(applicationContext).asBitmap().load("$uri").submit()
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

    private val cameraPermissionResult =
        registerForActivityResult(ApplicationDetailSettingsContract()) {
            if (checkSelfPermissionForCamera()
            ) {
                takeImage()
                GameSounds(this@MainActivity).playClickSound()
            }
        }

    @RequiresApi(34)
    private val galleryPermissionResult =
        registerForActivityResult(ApplicationDetailSettingsContract()) {
            if (checkSelfPermissionForGallery()
            ) {
                selectImageFromGallery()
                GameSounds(this@MainActivity).playClickSound()
            }
        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about_application -> AlertDialogDemonstrator(this).showAboutAppAlertDialog()
        }
        return true
    }

    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerPermissionListener()

        setClickListeners()

        getImagesFromAssets()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialogDemonstrator(this@MainActivity).showConfirmationAlertDialog()
            }

        })
    }

    @RequiresApi(34)
    fun registerPermissionListener() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                val isCameraPermissionGranted = permissions[Manifest.permission.CAMERA] == true

                if (isCameraPermissionGranted) {
                    takeImage()
                    GameSounds(this@MainActivity).playClickSound()
                }

                val isGalleryPermissionGranted =
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                            || permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
                            || permissions[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true

                if (isGalleryPermissionGranted) {
                    selectImageFromGallery()
                    GameSounds(this@MainActivity).playClickSound()
                }

            }
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
            applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile
        )
    }

    @RequiresApi(34)
    private fun setClickListeners() {
        binding.buttonCamera.setOnClickListener {
            checkCameraPermission()
        }
        binding.buttonGallery.setOnClickListener {
            checkGalleryPermission()
        }
    }

    @RequiresApi(34)
    private fun checkGalleryPermission() {
        if (checkSelfPermissionForGallery()
        ) {
            selectImageFromGallery()
            GameSounds(this@MainActivity).playClickSound()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
                || shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            ) {
                val galleryDeniedAlertDialog =
                    AlertDialogDemonstrator(this).showGalleryDeniedDialog()
                val buttonPositive = galleryDeniedAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                buttonPositive.setOnClickListener {
                    if (buttonPositive.isPressed) {
                        galleryPermissionResult.launch(determineManifestPermissionForGallery())
                        galleryDeniedAlertDialog.dismiss()
                    }
                }
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            }
        }
    }

    @RequiresApi(34)
    private fun checkSelfPermissionForGallery() =
        (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        )
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
                == PackageManager.PERMISSION_GRANTED)

    @RequiresApi(34)
    private fun determineManifestPermissionForGallery(): String {
        val manifestPermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_EXTERNAL_STORAGE
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        }
        return manifestPermission
    }

    @RequiresApi(34)
    private fun checkCameraPermission() {
        if (checkSelfPermissionForCamera()) {
            takeImage()
            GameSounds(this@MainActivity).playClickSound()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                val cameraDeniedAlertDialog =
                    AlertDialogDemonstrator(this).showCameraDeniedDialog()
                val buttonPositive: Button =
                    cameraDeniedAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                buttonPositive.setOnClickListener {
                    if (buttonPositive.isPressed) {
                        cameraPermissionResult.launch(Manifest.permission.CAMERA)
                        cameraDeniedAlertDialog.dismiss()
                    }
                }
            } else {
                permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            }
        }
    }

    private fun checkSelfPermissionForCamera() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

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
                        bitmap = Glide.with(applicationContext).asBitmap()
                            .load("file:///android_asset/$uri").submit().get()
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

    private fun screenOrientation() = if (bitmap.width > bitmap.height) "landscape" else "portrait"

}