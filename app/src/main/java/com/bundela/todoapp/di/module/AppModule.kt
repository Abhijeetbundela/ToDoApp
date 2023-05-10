package com.bundela.todoapp.di.module

import android.content.Context
import com.bundela.todoapp.database.dao.TodoDao
import com.bundela.todoapp.database.AppDatabase
import com.bundela.todoapp.repository.ToDoRepository
import com.bundela.todoapp.utils.datastore.DataStorePreference
import com.bundela.todoapp.viewModel.ToDoViewModel
import org.koin.android.annotation.KoinViewModel

import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.dsl.module
import java.util.prefs.Preferences

@Module
class AppModule {
    @Single
    fun dbInstance(context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Single
    fun toDoDao(appDatabase: AppDatabase): TodoDao = appDatabase.toDoDao()

    @Single
    fun todoRepository(dao: TodoDao): ToDoRepository = ToDoRepository(dao)

    @Single
    fun dataStorePreference(context: Context): DataStorePreference = DataStorePreference(context)

    @KoinViewModel
    fun todoViewModel(
        repository: ToDoRepository,
        dataStorePreference: DataStorePreference
    ): ToDoViewModel = ToDoViewModel(repository, dataStorePreference)
}


