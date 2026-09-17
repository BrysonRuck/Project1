package com.example.project1

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.database.UserEntity
import kotlinx.coroutines.launch

private const val EDIT_USERNAME = "username"
private const val EDIT_ADDRESS = "address"
private const val EDIT_DISTANCE = "distance"
private const val EDIT_PASSWORD = "password"

@Composable
fun SetPreferencesScreen(
    user: UserEntity?,
    loadMessage: String,
    onSaveUser: suspend (UserEntity) -> String?,
    onBackToProfile: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var pageMessage by remember { mutableStateOf("") }
    var editorMessage by remember { mutableStateOf("") }
    var editingField by remember { mutableStateOf<String?>(null) }
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

    fun openEditor(field: String) {
        pageMessage = ""
        editorMessage = ""
        editingField = field
    }

    fun returnToProfile() {
        if (editingField != null) {
            showDiscardDialog = true
        } else {
            onBackToProfile()
        }
    }

    BackHandler {
        returnToProfile()
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
                        onBackToProfile()
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

        if (user == null) {
            Text(
                text = loadMessage.ifBlank { "Loading profile..." }
            )
        } else {
            Text("Username: ${user.username}")

            if (editingField == null) {
                Button(
                    onClick = {
                        openEditor(EDIT_USERNAME)
                    }
                ) {
                    Text("Edit username")
                }
            }

            if (editingField == EDIT_USERNAME) {
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

                SaveDiscardButtons(
                    onSave = {
                        val newUsername = usernameInput.trim()

                        when {
                            newUsername.isBlank() -> {
                                editorMessage =
                                    "You need to enter a username to save changes."
                            }

                            newUsername.equals(user.username, ignoreCase = true) -> {
                                editorMessage =
                                    "That is your current username."
                            }

                            else -> {
                                coroutineScope.launch {
                                    val saveError = onSaveUser(
                                        user.copy(username = newUsername)
                                    )

                                    if (saveError == null) {
                                        pageMessage = "Username updated."
                                        closeEditor()
                                    } else {
                                        editorMessage = saveError
                                    }
                                }
                            }
                        }
                    },
                    onDiscard = {
                        closeEditor()
                    }
                )
            }

            Text("Address: ${user.address}")

            if (editingField == null) {
                Button(
                    onClick = {
                        openEditor(EDIT_ADDRESS)
                    }
                ) {
                    Text("Edit address")
                }
            }

            if (editingField == EDIT_ADDRESS) {
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

                SaveDiscardButtons(
                    onSave = {
                        val newAddress = addressInput.trim()

                        when {
                            newAddress.isBlank() -> {
                                editorMessage =
                                    "You need to enter an address to save changes."
                            }

                            newAddress.equals(user.address.trim(), ignoreCase = true) -> {
                                editorMessage =
                                    "That is your current address."
                            }

                            else -> {
                                coroutineScope.launch {
                                    val saveError = onSaveUser(
                                        user.copy(address = newAddress)
                                    )

                                    if (saveError == null) {
                                        pageMessage = "Address updated."
                                        closeEditor()
                                    } else {
                                        editorMessage = saveError
                                    }
                                }
                            }
                        }
                    },
                    onDiscard = {
                        closeEditor()
                    }
                )
            }

            Text("Preferred radius: ${user.distanceMiles} miles")

            if (editingField == null) {
                Button(
                    onClick = {
                        openEditor(EDIT_DISTANCE)
                    }
                ) {
                    Text("Edit preferred radius")
                }
            }

            if (editingField == EDIT_DISTANCE) {
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

                SaveDiscardButtons(
                    onSave = {
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

                            newDistance == user.distanceMiles -> {
                                editorMessage =
                                    "That is your current preferred radius."
                            }

                            else -> {
                                coroutineScope.launch {
                                    val saveError = onSaveUser(
                                        user.copy(distanceMiles = newDistance)
                                    )

                                    if (saveError == null) {
                                        pageMessage = "Preferred radius updated."
                                        closeEditor()
                                    } else {
                                        editorMessage = saveError
                                    }
                                }
                            }
                        }
                    },
                    onDiscard = {
                        closeEditor()
                    }
                )
            }

            Text("Password: ••••••••")

            if (editingField == null) {
                Button(
                    onClick = {
                        openEditor(EDIT_PASSWORD)
                    }
                ) {
                    Text("Edit password")
                }
            }

            if (editingField == EDIT_PASSWORD) {
                OutlinedTextField(
                    value = currentPasswordInput,
                    onValueChange = {
                        currentPasswordInput = it
                        editorMessage = ""
                    },
                    label = {
                        Text("Current password")
                    },
                    visualTransformation = PasswordVisualTransformation(),
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
                    visualTransformation = PasswordVisualTransformation(),
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
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true
                )

                SaveDiscardButtons(
                    onSave = {
                        when {
                            currentPasswordInput.isBlank() ||
                                    newPasswordInput.isBlank() ||
                                    newPasswordAgainInput.isBlank() -> {
                                editorMessage =
                                    "Fill in all password fields to save changes."
                            }

                            currentPasswordInput != user.password -> {
                                editorMessage =
                                    "Your current password is incorrect."
                            }

                            newPasswordInput != newPasswordAgainInput -> {
                                editorMessage =
                                    "The new passwords do not match."
                            }

                            newPasswordInput == user.password -> {
                                editorMessage =
                                    "Your new password must be different."
                            }

                            else -> {
                                coroutineScope.launch {
                                    val saveError = onSaveUser(
                                        user.copy(password = newPasswordInput)
                                    )

                                    if (saveError == null) {
                                        pageMessage = "Password updated."
                                        closeEditor()
                                    } else {
                                        editorMessage = saveError
                                    }
                                }
                            }
                        }
                    },
                    onDiscard = {
                        closeEditor()
                    }
                )
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

@Composable
private fun SaveDiscardButtons(
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onSave
        ) {
            Text("Save changes")
        }

        Button(
            onClick = onDiscard
        ) {
            Text("Discard changes")
        }
    }
}