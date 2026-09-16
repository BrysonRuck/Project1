package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.database.AppDatabase
import com.example.project1.database.UserEntity
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch

private enum class EditingField {
    USERNAME,
    ADDRESS,
    DISTANCE,
    PASSWORD
}

class SetPreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Project1Theme {
                val database = remember {
                    AppDatabase.getDatabase(applicationContext)
                }
                val coroutineScope = rememberCoroutineScope()

                var user by remember { mutableStateOf<UserEntity?>(null) }
                var pageMessage by remember { mutableStateOf("") }
                var editorMessage by remember { mutableStateOf("") }
                var editingField by remember { mutableStateOf<EditingField?>(null) }
                var showDiscardDialog by remember { mutableStateOf(false) }

                var usernameInput by remember { mutableStateOf("") }
                var addressInput by remember { mutableStateOf("") }
                var distanceInput by remember { mutableStateOf("") }

                var currentPasswordInput by remember { mutableStateOf("") }
                var newPasswordInput by remember { mutableStateOf("") }
                var newPasswordAgainInput by remember { mutableStateOf("") }

                fun closeEditor() {
                    editingField = null
                    editorMessage = ""

                    usernameInput = ""
                    addressInput = ""
                    distanceInput = ""

                    currentPasswordInput = ""
                    newPasswordInput = ""
                    newPasswordAgainInput = ""
                }

                fun openEditor(field: EditingField) {
                    pageMessage = ""
                    editorMessage = ""
                    editingField = field
                }

                fun returnToProfile() {
                    if (editingField != null) {
                        showDiscardDialog = true
                    } else {
                        finish()
                    }
                }

                BackHandler {
                    returnToProfile()
                }

                LaunchedEffect(Unit) {
                    try {
                        user = database.userDao().getUserById(CURRENT_USER_ID)

                        if (user == null) {
                            pageMessage = "Error: could not load the current user."
                        }
                    } catch (error: Exception) {
                        pageMessage = "Error: could not load the current user."
                    }
                }

                if (showDiscardDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDiscardDialog = false
                        },
                        title = {
                            Text("Discard changes?")
                        },
                        text = {
                            Text(
                                "You may have unsaved changes. Leaving this page will discard them. " +
                                        "Do you really want to discard them?"
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDiscardDialog = false
                                    closeEditor()
                                    finish()
                                }
                            ) {
                                Text("Discard and leave")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showDiscardDialog = false
                                }
                            ) {
                                Text("Keep editing")
                            }
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Update your profile",
                        fontSize = 24.sp
                    )

                    val currentUser = user

                    if (currentUser == null) {
                        Text(
                            text = if (pageMessage.isBlank()) {
                                "Loading profile..."
                            } else {
                                pageMessage
                            }
                        )
                    } else {
                        Text("Username: ${currentUser.username}")

                        if (editingField == null) {
                            Button(
                                onClick = {
                                    openEditor(EditingField.USERNAME)
                                }
                            ) {
                                Text("Edit username")
                            }
                        }

                        if (editingField == EditingField.USERNAME) {
                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = {
                                    usernameInput = it
                                    editorMessage = ""
                                },
                                label = {
                                    Text("New username")
                                },
                                singleLine = true
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val newUsername = usernameInput.trim()

                                        when {
                                            newUsername.isBlank() -> {
                                                editorMessage =
                                                    "You need to enter a username to save changes."
                                            }

                                            newUsername.equals(
                                                currentUser.username,
                                                ignoreCase = true
                                            ) -> {
                                                editorMessage =
                                                    "That is your current username."
                                            }

                                            else -> {
                                                coroutineScope.launch {
                                                    try {
                                                        val existingUser = database.userDao()
                                                            .getUserByUsernameIgnoringCase(newUsername)

                                                        if (
                                                            existingUser != null &&
                                                            existingUser.userId != currentUser.userId
                                                        ) {
                                                            editorMessage =
                                                                "That username is already in use."
                                                        } else {
                                                            val updatedUser = currentUser.copy(
                                                                username = newUsername
                                                            )

                                                            database.userDao().updateUser(updatedUser)
                                                            user = updatedUser
                                                            pageMessage = "Username updated."
                                                            closeEditor()
                                                        }
                                                    } catch (error: Exception) {
                                                        editorMessage =
                                                            "Error: could not update your username."
                                                    }
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Text("Save changes")
                                }

                                Button(
                                    onClick = {
                                        closeEditor()
                                    }
                                ) {
                                    Text("Discard changes")
                                }
                            }
                        }

                        Text("Address: ${currentUser.address}")

                        if (editingField == null) {
                            Button(
                                onClick = {
                                    openEditor(EditingField.ADDRESS)
                                }
                            ) {
                                Text("Edit address")
                            }
                        }

                        if (editingField == EditingField.ADDRESS) {
                            OutlinedTextField(
                                value = addressInput,
                                onValueChange = {
                                    addressInput = it
                                    editorMessage = ""
                                },
                                label = {
                                    Text("New address")
                                }
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val newAddress = addressInput.trim()

                                        when {
                                            newAddress.isBlank() -> {
                                                editorMessage =
                                                    "You need to enter an address to save changes."
                                            }

                                            newAddress.equals(
                                                currentUser.address.trim(),
                                                ignoreCase = true
                                            ) -> {
                                                editorMessage =
                                                    "That is your current address."
                                            }

                                            else -> {
                                                coroutineScope.launch {
                                                    try {
                                                        val updatedUser = currentUser.copy(
                                                            address = newAddress
                                                        )

                                                        database.userDao().updateUser(updatedUser)
                                                        user = updatedUser
                                                        pageMessage = "Address updated."
                                                        closeEditor()
                                                    } catch (error: Exception) {
                                                        editorMessage =
                                                            "Error: could not update your address."
                                                    }
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Text("Save changes")
                                }

                                Button(
                                    onClick = {
                                        closeEditor()
                                    }
                                ) {
                                    Text("Discard changes")
                                }
                            }
                        }

                        Text(
                            "Preferred radius: ${currentUser.distanceMiles} miles"
                        )

                        if (editingField == null) {
                            Button(
                                onClick = {
                                    openEditor(EditingField.DISTANCE)
                                }
                            ) {
                                Text("Edit preferred radius")
                            }
                        }

                        if (editingField == EditingField.DISTANCE) {
                            OutlinedTextField(
                                value = distanceInput,
                                onValueChange = {
                                    distanceInput = it
                                    editorMessage = ""
                                },
                                label = {
                                    Text("New radius in miles")
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val newDistance = distanceInput.toIntOrNull()

                                        when {
                                            distanceInput.isBlank() -> {
                                                editorMessage =
                                                    "You need to enter a radius to save changes."
                                            }

                                            newDistance == null || newDistance <= 0 -> {
                                                editorMessage =
                                                    "Enter a positive whole number of miles."
                                            }

                                            newDistance == currentUser.distanceMiles -> {
                                                editorMessage =
                                                    "That is your current preferred radius."
                                            }

                                            else -> {
                                                coroutineScope.launch {
                                                    try {
                                                        val updatedUser = currentUser.copy(
                                                            distanceMiles = newDistance
                                                        )

                                                        database.userDao().updateUser(updatedUser)
                                                        user = updatedUser
                                                        pageMessage =
                                                            "Preferred radius updated."
                                                        closeEditor()
                                                    } catch (error: Exception) {
                                                        editorMessage =
                                                            "Error: could not update your preferred radius."
                                                    }
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Text("Save changes")
                                }

                                Button(
                                    onClick = {
                                        closeEditor()
                                    }
                                ) {
                                    Text("Discard changes")
                                }
                            }
                        }

                        Text("Password: ••••••••")

                        if (editingField == null) {
                            Button(
                                onClick = {
                                    openEditor(EditingField.PASSWORD)
                                }
                            ) {
                                Text("Edit password")
                            }
                        }

                        if (editingField == EditingField.PASSWORD) {
                            OutlinedTextField(
                                value = currentPasswordInput,
                                onValueChange = {
                                    currentPasswordInput = it
                                    editorMessage = ""
                                },
                                label = {
                                    Text("Current password")
                                },
                                visualTransformation =
                                    PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newPasswordInput,
                                onValueChange = {
                                    newPasswordInput = it
                                    editorMessage = ""
                                },
                                label = {
                                    Text("New password")
                                },
                                visualTransformation =
                                    PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newPasswordAgainInput,
                                onValueChange = {
                                    newPasswordAgainInput = it
                                    editorMessage = ""
                                },
                                label = {
                                    Text("New password (again)")
                                },
                                visualTransformation =
                                    PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                singleLine = true
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        when {
                                            currentPasswordInput.isBlank() ||
                                                    newPasswordInput.isBlank() ||
                                                    newPasswordAgainInput.isBlank() -> {
                                                editorMessage =
                                                    "Fill in all password fields to save changes."
                                            }

                                            currentPasswordInput != currentUser.password -> {
                                                editorMessage =
                                                    "Your current password is incorrect."
                                            }

                                            newPasswordInput != newPasswordAgainInput -> {
                                                editorMessage =
                                                    "The new passwords do not match."
                                            }

                                            newPasswordInput == currentUser.password -> {
                                                editorMessage =
                                                    "Your new password must be different."
                                            }

                                            else -> {
                                                coroutineScope.launch {
                                                    try {
                                                        val updatedUser = currentUser.copy(
                                                            password = newPasswordInput
                                                        )

                                                        database.userDao().updateUser(updatedUser)
                                                        user = updatedUser
                                                        pageMessage = "Password updated."
                                                        closeEditor()
                                                    } catch (error: Exception) {
                                                        editorMessage =
                                                            "Error: could not update your password."
                                                    }
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Text("Save changes")
                                }

                                Button(
                                    onClick = {
                                        closeEditor()
                                    }
                                ) {
                                    Text("Discard changes")
                                }
                            }
                        }

                        if (editorMessage.isNotBlank()) {
                            Text(editorMessage)
                        }

                        if (pageMessage.isNotBlank()) {
                            Text(pageMessage)
                        }

                        Button(
                            onClick = {
                                returnToProfile()
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Back to profile")
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val CURRENT_USER_ID = 1L
        //CHANGE THIS TO THE ACTUAL USER THAT'S BEING CHANGED; THE CURRENT ONE SIGNED IN
    }
}