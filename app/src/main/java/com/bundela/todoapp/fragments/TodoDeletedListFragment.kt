package com.bundela.todoapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bundela.todoapp.R
import com.bundela.todoapp.adapter.TodoAdapter
import com.bundela.todoapp.database.model.TodoData
import com.bundela.todoapp.databinding.FragmentTodoDeletedListBinding
import com.bundela.todoapp.databinding.FragmentTodoListBinding
import com.bundela.todoapp.utils.actionMode.TodoListActionModeCallback
import com.bundela.todoapp.utils.appLogs

import com.bundela.todoapp.viewModel.ToDoViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class TodoDeletedListFragment : BaseFragment<FragmentTodoDeletedListBinding, ToDoViewModel>() {

    override val viewModel: ToDoViewModel by activityViewModel()

    private lateinit var todoAdapter: TodoAdapter

    private var actionModeCallback: TodoListActionModeCallback? = null

    var selectedPositions = ArrayList<Int>()
    var deletedTodo = ArrayList<TodoData>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTodoDeletedListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setUpRecyclerView()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.getDeletedTodo().observe(viewLifecycleOwner) { todo ->
            deletedTodo.clear()
            deletedTodo.addAll(todo)
            loadData(todo)
        }
    }

    private fun initUI() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_todo_menu, menu)
                menu.findItem(R.id.menu_list).isVisible = false

                menu.findItem(R.id.menu_night_mode).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                menu.findItem(R.id.menu_night_mode).title = getString(R.string.restore_all)
                menu.findItem(R.id.menu_deleted_todo).title = getString(R.string.delete_all)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_night_mode -> restoreAllTodo()
                    R.id.menu_deleted_todo -> deleteAllTodo()
                    android.R.id.home -> requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun restoreAllTodo() {
        if (deletedTodo.isEmpty()) return
        val ids = ArrayList<Int>()
        deletedTodo.forEach {
            ids.add(it.id)
        }
        viewModel.restoreById(ids)
        toast(getString(R.string.restored_msg))
        deletedTodo.clear()
    }

    private fun deleteAllTodo() {
        if (deletedTodo.isEmpty()) return
        val titleMessage = "Delete?"
        val descriptionMessage = "Are you sure you want to delete?"

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            val ids = ArrayList<Int>()
            deletedTodo.forEach {
                ids.add(it.id)
            }
            viewModel.deleteById(ids)
            toast(getString(R.string.deleted_msg))
            deletedTodo.clear()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle(titleMessage)
        builder.setMessage(descriptionMessage)
        builder.create().show()

    }

    private fun noDateFound() {
        binding.noDataLayout.root.visibility = View.VISIBLE
        binding.noDataLayout.tvTitle.text = getString(R.string.no_delete_todo_found)
        todoAdapter.differ.submitList(emptyList())
    }

    private fun loadData(data: List<TodoData>) {
        if (data.isNotEmpty()) {
            binding.noDataLayout.root.visibility = View.GONE
            todoAdapter.differ.submitList(data)
        } else {
            noDateFound()
        }
    }

    private fun setUpRecyclerView() {
        todoAdapter = TodoAdapter()

        binding.rvTodo.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = todoAdapter
        }

        todoAdapter.setOnItemClickListener { it, position ->
            if (actionModeCallback != null) {
                selectTodo(it, position)
            }
        }

        todoAdapter.setLongClickListener { it, position ->
            if (actionModeCallback != null) {
                selectTodo(it, position)
            } else {
                initActionMode(it, position)
            }
        }
    }

    private fun selectTodo(it: TodoData, position: Int) {
        if (todoAdapter.selectedTodo.contains(it)) {
            todoAdapter.selectedTodo.remove(it)
            selectedPositions.remove(position)
        } else {
            selectedPositions.add(position)
            todoAdapter.selectedTodo.add(it)
        }

        todoAdapter.notifyItemChanged(position)

        if (todoAdapter.selectedTodo.size != 0) {
            actionModeCallback?.mode?.title = "${todoAdapter.selectedTodo.size}"
        } else {
            todoAdapter.selectedTodo.clear()
            selectedPositions.forEach {
                todoAdapter.notifyItemChanged(it)
            }
            actionModeCallback?.finishActionMode()
            selectedPositions.clear()
            actionModeCallback = null
        }
    }

    private fun initActionMode(it: TodoData, position: Int) {
        todoAdapter.selectedTodo.clear()
        selectedPositions.clear()
        actionModeCallback = null

        todoAdapter.selectedTodo.add(it)
        selectedPositions.add(position)
        todoAdapter.notifyItemChanged(position)

        actionModeCallback = TodoListActionModeCallback()
        actionModeCallback?.startActionMode(
            R.menu.delete_action_menu,
            requireView(),
            "${todoAdapter.selectedTodo.size}"
        )
        actionModeCallback?.setOnActionItemClickListener {
            if (it == null) {
                todoAdapter.selectedTodo.clear()
                selectedPositions.forEach { index ->
                    todoAdapter.notifyItemChanged(index)
                }
                actionModeCallback?.finishActionMode()
                selectedPositions.clear()

                actionModeCallback = null
            } else {
                when (it.itemId) {
                    R.id.menu_delete -> deleteTodo()
                    R.id.menu_restore -> restoreTodo()
                }
            }
        }
    }

    private fun deleteTodo() {
        val titleMessage = "Delete?"
        val descriptionMessage = "Are you sure you want to delete?"

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            val ids = ArrayList<Int>()
            todoAdapter.selectedTodo.forEach {
                ids.add(it.id)
            }

            viewModel.deleteById(ids)

            toast(getString(R.string.deleted_msg))

            todoAdapter.selectedTodo.clear()

            selectedPositions.forEach {
                deletedTodo.removeAt(it)
            }

            selectedPositions.clear()

            actionModeCallback?.finishActionMode()
            actionModeCallback = null

        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle(titleMessage)
        builder.setMessage(descriptionMessage)
        builder.create().show()

    }

    private fun restoreTodo() {
        val ids = ArrayList<Int>()
        todoAdapter.selectedTodo.forEach {
            ids.add(it.id)
        }

        viewModel.restoreById(ids)

        toast(getString(R.string.restored_msg))

        todoAdapter.selectedTodo.clear()

        selectedPositions.forEach {
            deletedTodo.removeAt(it)
        }

        selectedPositions.clear()

        actionModeCallback?.finishActionMode()
        actionModeCallback = null


    }

}