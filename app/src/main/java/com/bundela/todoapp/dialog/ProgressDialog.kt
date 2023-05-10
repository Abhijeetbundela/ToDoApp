package com.bundela.todoapp.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.bundela.todoapp.R


class ProgressDialog(var activity: Activity) {
    var dialog: Dialog = Dialog(activity)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    activity,
                    R.color.loadingDialogBackgroundColor
                )
            )
        )
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_loading)
    }

    fun showDialog() {
        dialog.show()
    }

    fun hideDialog() {
        dialog.dismiss()
    }
}