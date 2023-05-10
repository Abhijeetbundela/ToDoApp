package com.bundela.todoapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bundela.todoapp.database.model.TodoData
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM TodoData WHERE isDeleted == 0 ORDER BY (CASE isPinned WHEN 1 THEN 1 WHEN 0 THEN 2 END), id DESC")
    fun getTodo(): LiveData<List<TodoData>>

    @Query("SELECT * FROM TodoData WHERE isDeleted == 1 ORDER BY id DESC")
    fun getDeletedTodo(): LiveData<List<TodoData>>

    @Query("SELECT * FROM TodoData WHERE title LIKE :searchQuery || description LIKE :searchQuery")
    fun search(searchQuery: String): LiveData<List<TodoData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: TodoData)

    @Update
    suspend fun update(toDoData: TodoData)

    @Query("UPDATE TodoData SET isDeleted = 1, isPinned = 0 WHERE id =:id")
    suspend fun softDelete(id: Int)

    @Query("UPDATE TodoData SET isDeleted = 1, isPinned = 0 WHERE id IN (:ids)")
    suspend fun softDeleteById(ids: List<Int>)

    @Query("UPDATE TodoData SET isDeleted = 0, isPinned = 0 WHERE id IN (:ids)")
    suspend fun restoreById(ids: List<Int>)

    @Delete
    suspend fun delete(toDoData: TodoData)

    @Query("DELETE FROM TodoData WHERE isDeleted = 1 AND id IN (:ids)")
    suspend fun deleteById(ids: List<Int>)

}