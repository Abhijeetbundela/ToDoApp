package com.bundela.todoapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.bundela.todoapp.dialog.ProgressDialog

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    private lateinit var _progressDialog: ProgressDialog

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected abstract val viewModel: VM
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)

        _progressDialog = ProgressDialog(requireActivity())

        return binding.root
    }

    fun loader(show: Boolean) {
        if (show) _progressDialog.showDialog()
        else _progressDialog.hideDialog()
    }

    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    fun toast(message: String) {
        if(message.isEmpty())return
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun applicationContext(): Context = requireActivity().applicationContext

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}