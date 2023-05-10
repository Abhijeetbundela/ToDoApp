package com.bundela.todoapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
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
import com.bundela.todoapp.databinding.FragmentTodoListBinding
import com.bundela.todoapp.utils.actionMode.TodoListActionModeCallback
import com.bundela.todoapp.utils.appLogs
import com.bundela.todoapp.utils.datastore.DataStorePreference

import com.bundela.todoapp.viewModel.ToDoViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class TodoListFragment : BaseFragment<FragmentTodoListBinding, ToDoViewModel>() {

    override val viewModel: ToDoViewModel by activityViewModel()

    private lateinit var todoAdapter: TodoAdapter

    private var isList = false
    private var getUIMode = false

    private var actionModeCallback: TodoListActionModeCallback? = null

    var selectedPositions = ArrayList<Int>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTodoListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setUpRecyclerView()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.getTodo().observe(viewLifecycleOwner) { todo ->
            loadData(todo)
        }
    }

    private fun initUI() {
        //TODO isList value

        binding.addBtn.setOnClickListener {
            actionModeCallback?.finishActionMode()
            todoAdapter.selectedTodo.clear()
            selectedPositions.forEach {
                todoAdapter.notifyItemChanged(it)
            }
            selectedPositions.clear()
            findNavController().navigate(R.id.action_todo_list_fragment_to_todo_add_fragment)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_todo_menu, menu)

                lifecycleScope.launch {
                    getUIMode = viewModel.getUIMode.first()
                    val item = menu.findItem(R.id.menu_night_mode)
                    setUIMode(item, getUIMode)

                    isList = viewModel.getUIListMode.first()
                    appLogs(null, "isList $isList")
                    updateMenuIcon(menu.findItem(R.id.menu_list))
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_night_mode -> {
                        getUIMode = !getUIMode
                        setUIMode(menuItem, getUIMode)
                    }
                    R.id.menu_list -> {
                        isList = !isList
                        updateMenuIcon(menuItem)
                    }

                    R.id.menu_deleted_todo -> {
                        findNavController().navigate(R.id.action_todo_list_fragment_to_todo_deleted_fragment)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun updateMenuIcon(menuItem: MenuItem) {
        menuItem.icon =
            if (isList) {
                viewModel.saveToDataStore(true, DataStorePreference.UI_LIST_MODE_KEY)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid)
            } else {
                viewModel.saveToDataStore(false, DataStorePreference.UI_LIST_MODE_KEY)
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_linear)
            }
        updateAdapter()
    }

    private fun updateAdapter() {
        val lm = if (isList) {
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        }
        binding.rvTodo.apply {
            layoutManager = lm
            adapter = todoAdapter
        }
    }

    private fun noDateFound() {
        binding.noDataLayout.root.visibility = View.VISIBLE
        todoAdapter.differ.submitList(emptyList())
    }

    private fun loadData(data: List<TodoData>) {
        if (data.isNotEmpty()) {

            val todoData = data[0]

            if (isTodoEmpty(todoData.title ?: "", todoData.description ?: "")) {
                viewModel.deleteData(todoData)
                toast(getString(R.string.empty_todo_delete))
            }

            binding.noDataLayout.root.visibility = View.GONE
            todoAdapter.differ.submitList(data)
        } else {
            noDateFound()
        }
    }

    private fun setUpRecyclerView() {
        todoAdapter = TodoAdapter()

        updateAdapter()

        todoAdapter.setOnItemClickListener { it, position ->
            if (actionModeCallback != null) {
                selectTodo(it, position)
            } else {
                val bundle = Bundle().apply {
                    putParcelable("todoData", it)
                }
                findNavController().navigate(
                    R.id.action_todo_list_fragment_to_todo_update_fragment,
                    bundle
                )
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
            R.menu.list_action_menu,
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
                    R.id.menu_pin -> {}
                    R.id.menu_delete -> deleteTodo()
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

            viewModel.softDeleteById(ids)

            toast(getString(R.string.deleted_msg))

            selectedPositions.forEach {
                todoAdapter.notifyItemRemoved(it)
            }

            todoAdapter.selectedTodo.clear()
            selectedPositions.clear()

            actionModeCallback?.finishActionMode()
            actionModeCallback = null

        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle(titleMessage)
        builder.setMessage(descriptionMessage)
        builder.create().show()

    }

    private fun setUIMode(item: MenuItem, isChecked: Boolean) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            viewModel.saveToDataStore(true, DataStorePreference.UI_MODE_KEY)
            item.setIcon(R.drawable.ic_night)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            viewModel.saveToDataStore(false, DataStorePreference.UI_MODE_KEY)
            item.setIcon(R.drawable.ic_day)
        }
    }

    private fun isTodoEmpty(title: String, description: String): Boolean {
        return title.isEmpty() || description.isEmpty()
    }

}