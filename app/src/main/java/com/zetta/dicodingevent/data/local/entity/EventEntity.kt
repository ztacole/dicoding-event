package com.zetta.dicodingevent.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val id: Int,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("mediaCover")
    val mediaCover: String,

    @ColumnInfo("beginTime")
    val beginTime: String,

    @ColumnInfo("active")
    val active: Int,

    @ColumnInfo("isFavorite")
    val isFavorite: Boolean,

    @ColumnInfo("updatedAt")
    val updatedAt: Long,
)

