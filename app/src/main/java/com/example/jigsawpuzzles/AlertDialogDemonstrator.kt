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

}
