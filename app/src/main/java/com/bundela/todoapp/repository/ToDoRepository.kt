package com.bundela.todoapp.repository

import androidx.lifecycle.LiveData
import com.bundela.todoapp.database.dao.TodoDao
import com.bundela.todoapp.database.model.TodoData


class ToDoRepository(private val dao: TodoDao) {

    fun getTodo() : LiveData<List<TodoData>> = dao.getTodo()

    fun getDeletedTodo() : LiveData<List<TodoData>> = dao.getDeletedTodo()

    fun search(searchQuery: String) : LiveData<List<TodoData>> = dao.search(searchQuery)

    suspend fun insert(data: TodoData) = dao.insert(data)

    suspend fun update(data: TodoData) = dao.update(data)

    suspend fun softDelete(id: Int) = dao.softDelete(id)

    suspend fun softDeleteById(ids: List<Int>) = dao.softDeleteById(ids)

    suspend fun delete(data: TodoData) = dao.delete(data)

    suspend fun deleteById(ids: List<Int>) = dao.deleteById(ids)

    suspend fun restoreById(ids: List<Int>) = dao.restoreById(ids)
}