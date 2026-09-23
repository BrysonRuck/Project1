package com.example.project1

import android.content.Context
import androidx.core.content.edit

object UserSession {
    private const val PREFERENCES_NAME = "user_session"
    private const val USER_ID_KEY = "logged_in_user_id"
    private const val NO_USER_ID = -1L

    fun saveUserId(context: Context, userId: Long) {
        context.applicationContext
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit {
                putLong(USER_ID_KEY, userId)
            }
    }

    fun getUserId(context: Context): Long? {
        val userId = context.applicationContext
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getLong(USER_ID_KEY, NO_USER_ID)

        return userId.takeIf { it != NO_USER_ID }
    }

    fun clearUserId(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit {
                remove(USER_ID_KEY)
            }
    }
}