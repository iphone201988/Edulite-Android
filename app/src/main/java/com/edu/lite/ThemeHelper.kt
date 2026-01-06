package com.edu.lite


import android.content.Context



object ThemeHelper {
    private const val PREF_NAME = "app_theme_pref"
    private const val KEY_THEME = "selected_theme"

    // keys we will save: "green", "purple", "blue", "orange"
    fun saveThemeName(context: Context, themeName: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, themeName).apply()
    }

    fun getSavedThemeName(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_THEME, "green") ?: "green"
    }

    // Return the theme resource id for an Activity to call setTheme(resId)
    fun getThemeResId(context: Context): Int {
        return when (getSavedThemeName(context)) {
            "green" -> R.style.AppTheme_Green
            "purple" -> R.style.AppTheme_Purple
            "blue" -> R.style.AppTheme_Blue
            "orange" -> R.style.AppTheme_Orange
            else -> R.style.AppTheme_Green
        }
    }
}
