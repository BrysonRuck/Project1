package com.example.project1

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.project1.database.AppDatabase
import com.example.project1.database.UserDao
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.project1.MainActivity
import com.example.project1.database.UserEntity


class CreateAccActivity : ComponentActivity() {
    //This method's responsibility is to setup fields and button clicks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userDao = AppDatabase
            .getDatabase(this)
            .userDao()

        setContent {
            Project1Theme {
                val snackbarHostState = remember {
                    SnackbarHostState()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { innerPadding ->
                    CreateAccScreen(
                        modifier = Modifier.padding(innerPadding),
                        onBack = {
                            navigateToMainActivity()
                        },
                        onCreateAccount = { username, password, address ->
                            createAcc(                               userDao = userDao,
                                username = username,
                                password = password,
                                address = address,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    )
                }
            }
        }
    }

    private fun createAcc(
        userDao: UserDao,
        username: String,
        password: String,
        address: String,
        snackbarHostState: SnackbarHostState,
    ) {
        lifecycleScope.launch {
            when (
                createUser(
                    userDao = userDao,
                    username = username,
                    password = password,
                    address = address
                )
            ) {
                AccCreationResult.SUCCESS -> {
                    startActivity(
                        Intent(
                            this@CreateAccActivity,
                            LoginActivity::class.java
                        )
                    )
                    finish()
                }

                AccCreationResult.EMPTY_FIELD -> {
                    snackbarHostState.showSnackbar(
                        "Please complete all fields"
                    )
                }

                AccCreationResult.USERNAME_TAKEN -> {
                    snackbarHostState.showSnackbar(
                        "Username already exists"
                    )
                }
            }
        }
    }
    private suspend fun createUser(
        userDao: UserDao,
        username: String,
        password: String,
        address: String,
    ): AccCreationResult {
        if (
            username.isBlank() ||
            password.isBlank() ||
            address.isBlank()
        ) {
            return AccCreationResult.EMPTY_FIELD
        }

        if (userDao.getUserByUsername(username) != null) {
            return AccCreationResult.USERNAME_TAKEN
        }

        userDao.insertUser(
            UserEntity(
                username = username,
                password = password,
                address = address
            )
        )

        return AccCreationResult.SUCCESS
    }

    private fun navigateToMainActivity() {
        startActivity(
            Intent(this, MainActivity::class.java)
        )
        finish()
    }
}

//this helps me manage the oh fuck i need to write tests fuck my chud life
private enum class AccCreationResult {
    SUCCESS,
    EMPTY_FIELD,
    USERNAME_TAKEN
}

@Composable
private fun CreateAccScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onCreateAccount: (String, String, String) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create an Account")

        Button(onClick = onBack) {
            Text("Back")
        }

        TextField(
            modifier = Modifier.testTag("usernameField"),
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true
        )

        TextField(
            modifier = Modifier.testTag("passwordField"),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true
        )

        TextField(
            modifier = Modifier.testTag("addressField"),
            value = address,
            onValueChange = { address = it },
            label = { Text("Rough location") },
            singleLine = true
        )

        Button(
            onClick = {
                onCreateAccount(username, password, address)
            }
        ) {
            Text("Create account")
        }
    }
}

