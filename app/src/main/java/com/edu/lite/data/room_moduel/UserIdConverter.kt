package com.edu.lite.data.room_moduel

import androidx.room.TypeConverter
import com.google.gson.Gson

class UserIdConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromUserId(userId: UserId?): String? {
        return gson.toJson(userId)
    }

    @TypeConverter
    fun toUserId(json: String?): UserId? {
        return gson.fromJson(json, UserId::class.java)
    }
}
