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
                            //navigation
                        },
                        onCreateAccount = { username, password, address ->
                            createAcc(
                                userDao = userDao,
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
}