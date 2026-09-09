package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project1.database.AppDatabase
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch

class SetPreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Project1Theme {
                var address by remember { mutableStateOf("") }
                var distance by remember { mutableStateOf("") }
                var statusMessage by remember { mutableStateOf("") }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    try {
                        val database = AppDatabase.getDatabase(applicationContext)
                        val user = database.userDao().getUserById(DEFAULT_USER_ID)

                        if (user != null) {
                            address = user.address
                            distance = user.distanceMiles.toString()
                        } else {
                            statusMessage = "Error: could not load preferences."
                        }
                    } catch (error: Exception) {
                        statusMessage = "Error: could not load preferences."
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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

                            if (address.isBlank() || distanceMiles == null) {
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
                                                distanceMiles = distanceMiles
                                            )

                                            database.userDao().updateUser(updatedUser)
                                            statusMessage =
                                                "Your preferences have been updated."
                                        }
                                    } catch (error: Exception) {
                                        statusMessage =
                                            "Error: could not update preferences."
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Save preferences")
                    }

                    Text(statusMessage)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_USER_ID = 1L
        //CHANGE THIS TO THE ACTUAL USER THAT'S BEING CHANGED; THE CURRENT ONE SIGNED IN
    }
}