package com.bundela.todoapp.database.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class TodoData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var title: String?,
    @ColumnInfo var description: String?,
    @ColumnInfo var isPinned: Boolean,
    @ColumnInfo var createdDate: Date,
    @ColumnInfo var updatedDate: Date,
    @ColumnInfo var isDeleted: Int = 0,
    @ColumnInfo var priority: Priority,
) : Parcelable
