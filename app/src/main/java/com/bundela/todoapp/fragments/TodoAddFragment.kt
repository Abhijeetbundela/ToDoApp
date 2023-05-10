package com.bundela.todoapp.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bundela.todoapp.R
import com.bundela.todoapp.database.model.Priority
import com.bundela.todoapp.database.model.TodoData
import com.bundela.todoapp.databinding.FragmentAddTodoBinding
import com.bundela.todoapp.utils.GenericPriorityListener

import com.bundela.todoapp.viewModel.ToDoViewModel
import java.util.Date


class TodoAddFragment : BaseFragment<FragmentAddTodoBinding, ToDoViewModel>() {

    override val viewModel: ToDoViewModel by activityViewModels()

    private var isPinned = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddTodoBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initMenu()
    }

    private fun initUI() {
        binding.prioritySpinner.onItemSelectedListener = GenericPriorityListener(requireContext())
    }

    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_todo_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_save -> insertData()
                    R.id.menu_pin -> updatePinUI(menuItem)
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updatePinUI(menuItem: MenuItem) {
        isPinned = !isPinned
        menuItem.icon =
            if (isPinned) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_pinned)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_pin)
            }
    }

    private fun insertData() {
        val mPriority = binding.prioritySpinner.selectedItem.toString()
        val mTitle = binding.etTitle.text.toString()
        val mDescription = binding.etDescription.text.toString()

        // Insert Data to Database
        val data = TodoData(
            0,
            mTitle,
            mDescription,
            isPinned,
            Date(),
            Date(),
            0,
            parsePriority(mPriority),
        )
        viewModel.insertData(data)
        val isEmpty = isTodoEmpty(
            mTitle,
            mDescription
        )
        toast(if (isEmpty) "" else getString(R.string.saved_msg)).also {
            findNavController().navigate(
                R.id.action_todo_add_fragment_to_todo_list_fragment
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

}