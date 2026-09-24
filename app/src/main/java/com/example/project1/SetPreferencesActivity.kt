package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.project1.database.AppDatabase
import com.example.project1.database.UserEntity
import com.example.project1.ui.theme.Project1Theme

class SetPreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserId = intent.getLongExtra(
            EXTRA_USER_ID,
            UserSession.getUserId(this) ?: NO_USER_ID
        )

        setContent {
            Project1Theme {
                val database = remember {
                    AppDatabase.getDatabase(applicationContext)
                }

                var user by remember { mutableStateOf<UserEntity?>(null) }
                var loadMessage by remember { mutableStateOf("") }

                LaunchedEffect(currentUserId) {
                    if (currentUserId == NO_USER_ID) {
                        loadMessage = "Error: could not load the current user."
                        return@LaunchedEffect
                    }

                    try {
                        user = database.userDao().getUserById(currentUserId)

                        if (user == null) {
                            loadMessage = "Error: could not load the current user."
                        }
                    } catch (_: Exception) {
                        loadMessage = "Error: could not load the current user."
                    }
                }

                SetPreferencesScreen(
                    user = user,
                    loadMessage = loadMessage,
                    onSaveUser = { updatedUser ->
                        try {
                            val existingUser = database.userDao()
                                .getUserByUsernameIgnoringCase(updatedUser.username)

                            if (
                                existingUser != null &&
                                existingUser.userId != updatedUser.userId
                            ) {
                                "That username is already in use."
                            } else {
                                database.userDao().updateUser(updatedUser)
                                user = updatedUser
                                null
                            }
                        } catch (_: Exception) {
                            "Error: could not save your changes."
                        }
                    },
                    onBackToProfile = {
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        private const val NO_USER_ID = -1L
    }

    //i'm finally done with this god awful user id session thing holy FUCK man
}