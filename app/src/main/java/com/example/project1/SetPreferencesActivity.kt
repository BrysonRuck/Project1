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
                    } catch (_: Exception) {
                        statusMessage = "Error: could not load preferences."
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("Address")

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it }
                    )

                    Text("Distance (in miles) for radius")

                    OutlinedTextField(
                        value = distance,
                        onValueChange = { distance = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    Button(
                        onClick = {
                            val distanceMiles = distance.toIntOrNull()

                            if ((address.isBlank()) || (distanceMiles == null)) {
                                statusMessage = "Error: could not update preferences."
                            } else {
                                coroutineScope.launch {
                                    try {
                                        val database = AppDatabase.getDatabase(applicationContext)
                                        val user = database.userDao().getUserById(DEFAULT_USER_ID)

                                        if (user == null) {
                                            statusMessage =
                                                "Error: could not update preferences."
                                        } else {
                                        val updatedUser = user.copy(
                                            address = address.trim(),
                                            distanceMiles = distanceMiles,
                                        )

                                            database.userDao().updateUser(updatedUser)
                                            statusMessage =
                                                "Your preferences have been updated."
                                        }
                                    } catch (_: Exception) {
                                        statusMessage =
                                            "Error: could not update preferences."
                                    }
                                }
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
        private const val CURRENT_USER_ID = 1L
        //CHANGE THIS TO THE ACTUAL USER THAT'S BEING CHANGED; THE CURRENT ONE SIGNED IN
        // REMEMBER THIS
    }
}