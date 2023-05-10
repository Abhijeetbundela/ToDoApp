package com.bundela.todoapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bundela.todoapp.R
import com.bundela.todoapp.database.model.Priority
import com.bundela.todoapp.database.model.TodoData
import com.bundela.todoapp.databinding.FragmentTodoUpdateBinding
import com.bundela.todoapp.utils.GenericPriorityListener
import com.bundela.todoapp.utils.appLogs
import com.bundela.todoapp.viewModel.ToDoViewModel
import java.util.Date


class TodoUpdateFragment : BaseFragment<FragmentTodoUpdateBinding, ToDoViewModel>() {

    override val viewModel: ToDoViewModel by activityViewModels()

    private val args: TodoUpdateFragmentArgs by navArgs()

    private var isPinned = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTodoUpdateBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initMenu()
    }

    private fun initUI() {
        with(binding) {
            prioritySpinner.onItemSelectedListener = GenericPriorityListener(requireContext())

            etTitle.setText(args.todoData.title ?: "")
            etDescription.setText(args.todoData.description ?: "")

            prioritySpinner.setSelection(setPriority(args.todoData.priority))
            
            isPinned = args.todoData.isPinned
        }
    }

    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_todo_menu, menu)
                updatePinUI(menu.findItem(R.id.menu_pin))
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_save -> updateData()
                    R.id.menu_pin -> {
                        isPinned = !isPinned
                        updatePinUI(menuItem)
                    }
                    R.id.menu_delete -> deleteData()
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun deleteData() {
        val data = args.todoData
        val titleMessage = if (data.title.isNullOrBlank()) {
            "Delete?"
        } else {
            "Delete '${data.title}'?"
        }

        val descriptionMessage = if (data.title.isNullOrBlank()) {
            "Are you sure you want to delete?"
        } else {
            "Are you sure you want to delete '${data.title}'?"
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.softDeleteData(args.todoData)
            toast(getString(R.string.deleted_msg))
            findNavController().navigate(R.id.action_todo_update_fragment_to_todo_list_fragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle(titleMessage)
        builder.setMessage(descriptionMessage)
        builder.create().show()
    }

    private fun updatePinUI(menuItem: MenuItem) {
        menuItem.icon =
            if (isPinned) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_pinned)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_pin)
            }
    }

    private fun updateData() {
        val mPriority = binding.prioritySpinner.selectedItem.toString()
        val mTitle = binding.etTitle.text.toString()
        val mDescription = binding.etDescription.text.toString()

        // update Data to Database
        val data = TodoData(
            args.todoData.id,
            mTitle,
            mDescription,
            isPinned,
            args.todoData.createdDate,
            Date(),
            0,
            parsePriority(mPriority),
        )
        viewModel.updateData(data)
        val isEmpty = isTodoEmpty(
            mTitle,
            mDescription
        )
        toast(if (isEmpty) "" else getString(R.string.updated_msg)).also {
            findNavController().navigate(
                R.id.action_todo_update_fragment_to_todo_list_fragment
            )
        }

    }

    private fun isTodoEmpty(title: String, description: String): Boolean {
        return title.isEmpty() || description.isEmpty()
    }

    private fun parsePriority(priority: String): Priority {
        return when (priority) {
            "High Priority" -> Priority.HIGH
            "Medium Priority" -> Priority.MEDIUM
            "Low Priority" -> Priority.LOW
            else -> Priority.MEDIUM
        }
    }

    private fun setPriority(priority: Priority): Int {
        return when (priority) {
            Priority.MEDIUM -> 0
            Priority.HIGH -> 1
            Priority.LOW -> 2
        }
    }

}