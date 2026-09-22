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
import com.example.project1.MainActivity
import com.example.project1.database.UserEntity


class CreateAccActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        setContent {
            Project1Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Create an Account")
                        CreateAccountScreen(userDao)
                    }
                }
            }
        }
    }

    @Composable
    fun CreateAccountScreen(userDao: UserDao) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    context.startActivity(
                        Intent(context, MainActivity::class.java)
                    )
                }
            ) {
                Text("Back")
            }

            TextField(
                modifier = Modifier.testTag("usernameField"),
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )

            TextField(
                modifier = Modifier.testTag("passwordField"),
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )

            TextField(
                modifier = Modifier.testTag("addressField"),
                value = address,
                onValueChange = { address = it },
                label = { Text("Rough location") }
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val newUser = UserEntity( username = username, password = password, address = address
                        )

                        userDao.insertUser(newUser)
                    }
                }
            ) {
                Text("Create account")
            }
        }
    }
}