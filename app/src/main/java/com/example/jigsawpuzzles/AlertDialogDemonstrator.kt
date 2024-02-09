package com.example.jigsawpuzzles

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import kotlin.system.exitProcess

class AlertDialogDemonstrator(val context: Context) {

    private val intent = Intent(context as Activity, MainActivity::class.java)

    fun showSuccessAlertDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.you_won_title))
            setIcon(R.drawable.ic_celebration)
            setMessage(context.getString(R.string.you_won_message) + "\n" + context.getString(R.string.you_won_question))
            setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                context.startActivity(intent)
                exitProcess(0)
            }
            setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                showConfirmationAlertDialog()
            }
            setCancelable(false)
        }.create().show()

    }


    fun showConfirmationAlertDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.confirmation))
            setIcon(R.drawable.ic_warning_24)
            setMessage(context.getString(R.string.are_you_sure))
            setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                exitProcess(0)
            }
            setNegativeButton(context.getString(R.string.no)) { _, _ ->
            }
            setCancelable(false)
        }.create().show()

    }

    fun showAboutAppAlertDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.about_application))
            setMessage(context.getString(R.string.authors))
            setPositiveButton(context.getString(R.string.close)) { _, _ ->
            }
            setCancelable(false)
        }.create().show()

    }

    fun showCameraDeniedDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.permission_denied))
            setIcon(R.drawable.ic_camera_24)
            setMessage(context.getString(R.string.message_for_rationale_for_camera))
            setPositiveButton(context.getString(R.string.go_to_permissions)) { _, _ ->
            }
            setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
            }
            setCancelable(false)
        }
        val alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }

    fun showGalleryDeniedDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.permission_denied))
            setIcon(R.drawable.ic_folder_24)
            setMessage(context.getString(R.string.message_for_rationale_for_gallery))
            setPositiveButton(context.getString(R.string.go_to_permissions)) { _, _ ->
            }
            setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
            }
            setCancelable(false)
        }
        val alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }


}

