package com.edu.lite.data.room_moduel

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "VideoDownload")
data class DownloadVideoData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val _id: String?,
    val createdAt: String?,
    val grade: String?,
    val subject: String?,
    val thumbnailUrl: String?,
    val time: Int?,
    val title: String?,
    val videoDownload: Boolean,
    val localPath: String? = null,
    @Embedded(prefix = "user_") val userId: UserId?,
)

data class UserId(
    val _id: String?, val email: String?, val name: String?
)