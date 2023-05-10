package com.bundela.todoapp.viewModel

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bundela.todoapp.database.model.TodoData
import com.bundela.todoapp.repository.ToDoRepository
import com.bundela.todoapp.utils.datastore.DataStorePreference
import kotlinx.coroutines.launch


class ToDoViewModel(
    private val repository: ToDoRepository,
    private val uiDataStore: DataStorePreference,
) : ViewModel() {

    fun getTodo(): LiveData<List<TodoData>> = repository.getTodo()

    fun getDeletedTodo(): LiveData<List<TodoData>> = repository.getDeletedTodo()

    fun search(searchQuery: String): LiveData<List<TodoData>> = repository.search(searchQuery)

    fun insertData(data: TodoData) = viewModelScope.launch {
        repository.insert(data)
    }

    fun updateData(data: TodoData) = viewModelScope.launch {
        repository.update(data)
    }

    fun softDeleteData(data: TodoData) = viewModelScope.launch {
        repository.softDelete(data.id)
    }

    fun softDeleteById(ids: List<Int>) = viewModelScope.launch {
        repository.softDeleteById(ids)
    }

    fun deleteData(data: TodoData) = viewModelScope.launch {
        repository.delete(data)
    }

    fun deleteById(ids: List<Int>) = viewModelScope.launch {
        repository.deleteById(ids)
    }

    fun restoreById(ids: List<Int>) = viewModelScope.launch {
        repository.restoreById(ids)
    }

    val getUIMode = uiDataStore.uiMode
    val getUIListMode = uiDataStore.uiListMode

    fun saveToDataStore(isNightMode: Boolean, key: Preferences.Key<Boolean>) {
        viewModelScope.launch {
            uiDataStore.saveToDataStore(isNightMode, key)
        }
    }
}