package com.example.jigsawpuzzles

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.Nullable
import java.io.*


class ImageOrientationUtil {
    private val SCHEME_FILE = "file"
    private val SCHEME_CONTENT = "content"

    fun closeSilently(c: Closeable?) {
        if (c == null) return
        try {
            c.close()
        } catch (t: Throwable) {
            // Do nothing
        }
    }

    fun getExifRotation(imageFile: File?): Int {
        return if (imageFile == null) 0 else try {
            val exif = ExifInterface(imageFile.absolutePath)
            when (exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> ExifInterface.ORIENTATION_UNDEFINED
            }
        } catch (e: IOException) {
            //  Log.e("Error getting Exif data", e);
            0
        }
    }

    fun getFromMediaUri(
        context: Context?,
        resolver: ContentResolver, uri: Uri?,
    ): File? {
        if (uri == null) return null
        if (SCHEME_FILE == uri.scheme) {
            return File(uri.path)
        } else if (SCHEME_CONTENT == uri.scheme) {
            val filePathColumn =
                arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
            var cursor: Cursor? = null
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = if (uri.toString()
                            .startsWith("content://com.google.android.gallery3d")
                    ) cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) else cursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA
                    )
                    // Picasa images on API 13+
                    if (columnIndex != -1) {
                        val filePath = cursor.getString(columnIndex)
                        if (!TextUtils.isEmpty(filePath)) {
                            return File(filePath)
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                // Google Drive images
                return context?.let { getFromMediaUriPfd(it, resolver, uri) }
            } catch (ignored: SecurityException) {
                // Nothing we can do
            } finally {
                cursor?.close()
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun getTempFilename(context: Context): String? {
        val outputDir = context.cacheDir
        val outputFile = File.createTempFile("image", "tmp", outputDir)
        return outputFile.absolutePath
    }


    @Nullable
    private fun getFromMediaUriPfd(context: Context, resolver: ContentResolver, uri: Uri?): File? {
        if (uri == null) return null
        var input: FileInputStream? = null
        var output: FileOutputStream? = null
        try {
            val pfd = resolver.openFileDescriptor(uri, "r")
            val fd = pfd!!.fileDescriptor
            input = FileInputStream(fd)
            val tempFilename = getTempFilename(context)
            output = FileOutputStream(tempFilename)
            var read: Int
            val bytes = ByteArray(4096)
            while (input.read(bytes).also { read = it } != -1) {
                output.write(bytes, 0, read)
            }
            return File(tempFilename)
        } catch (ignored: IOException) {
            // Nothing we can do
        } finally {
            closeSilently(input)
            closeSilently(output)
        }
        return null
    }
}