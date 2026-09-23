package com.example.project1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.project1.database.AppDatabase
import com.example.project1.database.UserDao
import com.example.project1.database.UserEntity
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch

class CreateAccActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userDao = AppDatabase.getDatabase(this).userDao()

        setContent {
            Project1Theme {
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { innerPadding ->
                    CreateAccPage(
                        userDao = userDao,
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateAccPage(
    userDao: UserDao,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordAgain by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create an account")

        OutlinedTextField(
            modifier = Modifier.testTag("createUsernameField"),
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.testTag("createPasswordField"),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.testTag("createPasswordAgainField"),
            value = passwordAgain,
            onValueChange = { passwordAgain = it },
            label = { Text("Password (again)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true
        )

        Button(
            onClick = {
                val enteredUsername = username.trim()

                coroutineScope.launch {
                    when {
                        enteredUsername.isBlank() ||
                                password.isBlank() ||
                                passwordAgain.isBlank() -> {
                            snackbarHostState.showSnackbar(
                                "Fill in every field to create an account."
                            )
                        }

                        password != passwordAgain -> {
                            snackbarHostState.showSnackbar(
                                "The passwords do not match."
                            )
                        }

                        userDao.getUserByUsernameIgnoringCase(enteredUsername) != null -> {
                            snackbarHostState.showSnackbar(
                                "That username is already in use."
                            )
                        }

                        else -> {
                            try {
                                val newUserId = userDao.insertUser(
                                    UserEntity(
                                        username = enteredUsername,
                                        password = password
                                    )
                                )

                                UserSession.saveUserId(context, newUserId)

                                context.startActivity(
                                    Intent(context, MainActivity::class.java)
                                )
                                (context as? Activity)?.finish()
                            } catch (_: Exception) {
                                snackbarHostState.showSnackbar(
                                    "Could not create your account."
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Create account")
        }
    }
}