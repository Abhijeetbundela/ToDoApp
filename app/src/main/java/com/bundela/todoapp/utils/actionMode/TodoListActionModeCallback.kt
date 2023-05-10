package com.bundela.todoapp.utils.actionMode

import android.content.Context
import android.graphics.Rect
import android.view.Menu
import android.view.MenuItem

import androidx.annotation.MenuRes
import android.view.ActionMode
import android.view.View

class TodoListActionModeCallback : ActionMode.Callback {

    private var onActionItemClickListener: ((MenuItem?) -> Unit)? = null

    var mode: ActionMode? = null

    private var menuResId: Int = 0
    private var title: String? = null

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        this.mode = mode
        mode.menuInflater.inflate(menuResId, menu)
        mode.title = title
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        onActionItemClickListener?.let { it(null) }
        this.mode = null
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        onActionItemClickListener?.let { it(item) }
        return true
    }

    fun setOnActionItemClickListener(listener: (MenuItem?) -> Unit) {
        onActionItemClickListener = listener
    }

    fun startActionMode(
        menuResId: Int,
        view: View?,
        title: String? = null,
    ) {
        this.menuResId = menuResId
        this.title = title
        view?.startActionMode(this)
    }

    fun finishActionMode() {
        mode?.finish()
    }

}
