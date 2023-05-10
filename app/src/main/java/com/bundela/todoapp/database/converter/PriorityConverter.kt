package com.bundela.todoapp.database.converter

import androidx.room.TypeConverter
import com.bundela.todoapp.database.model.Priority

class PriorityConverter {

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }

}