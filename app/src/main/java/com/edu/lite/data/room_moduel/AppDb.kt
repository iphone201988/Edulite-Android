package com.edu.lite.data.room_moduel

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [DownloadVideoData::class], version = 2)
@TypeConverters(UserIdConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun connectAbstractClassConnection(): RoomDataBaseQueryPage
}
