package com.edu.lite.base.local

import android.content.SharedPreferences
import com.edu.lite.data.model.SignupData
import com.edu.lite.utils.saveValue
import com.google.gson.Gson
import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val IS_FIRST = "is_first"
        const val LANGUAGE = "language"
        const val USER_DATA = "user_data"
        const val TOKEN = "token"
        const val LOCALE = "locale_language"
    }

    private val gson = Gson()

    // ---------------------- User Data ---------------------- //
    fun setLoginData(data: SignupData) {
        sharedPreferences.edit().putString(KEY.USER_DATA, gson.toJson(data)).apply()
    }

    fun getLoginData(): SignupData? {
        val json = sharedPreferences.getString(KEY.USER_DATA, null)
        return json?.let { gson.fromJson(it, SignupData::class.java) }
    }



    fun setToken(isFirst: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.TOKEN, isFirst)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY.TOKEN, "")
    }


    fun saveLanguage(token: String) {
        sharedPreferences.edit().apply {
            putString(KEY.LANGUAGE, token)
            apply()
        }
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(KEY.LANGUAGE, "en") ?: "en"
    }


    fun clearAllExceptLanguage() {
        val lang = getLanguage()
        sharedPreferences.edit().clear().apply()
        saveLanguage(lang)
    }


}