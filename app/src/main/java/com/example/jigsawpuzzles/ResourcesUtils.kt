package com.example.jigsawpuzzles

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.view.ContextThemeWrapper

class ResourcesUtils(val context: Context) : ContextThemeWrapper() {
    fun isScreenOrientationPortrait(): Boolean {
        return when (context.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> true
            else -> false
        }
    }


}