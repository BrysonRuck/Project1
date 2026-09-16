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

        setContent {
            Project1Theme {
                val database = remember {
                    AppDatabase.getDatabase(applicationContext)
                }

                var user by remember { mutableStateOf<UserEntity?>(null) }
                var loadMessage by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    try {
                        user = database.userDao().getUserById(CURRENT_USER_ID)

                        if (user == null) {
                            loadMessage = "Error: could not load the current user."
                        }
                    } catch (error: Exception) {
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
                        } catch (error: Exception) {
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
        private const val CURRENT_USER_ID = 1L
        //CHANGE THIS TO THE ACTUAL USER THAT'S BEING CHANGED; THE CURRENT ONE SIGNED IN
    }
}