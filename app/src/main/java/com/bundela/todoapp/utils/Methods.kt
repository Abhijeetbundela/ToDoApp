package com.bundela.todoapp.utils

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bundela.todoapp.R

fun appLogs(tag: String?, msg: Any) {
    Log.d(tag ?: "MYT", "$msg")
}

class GenericPriorityListener internal constructor( private val context: Context) : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        when (position) {
            0 -> {
                (parent?.getChildAt(0) as TextView).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.yellow
                    )
                )
            }

            1 -> {
                (parent?.getChildAt(0) as TextView).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            }

            2 -> {
                (parent?.getChildAt(0) as TextView).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}