package com.example.jigsawpuzzles.extentions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class ApplicationDetailSettingsContract: ActivityResultContract<String?, Boolean>() {
    override fun createIntent(context: Context, input: String?): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return false
    }
}