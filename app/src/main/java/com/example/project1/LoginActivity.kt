package com.example.project1
//shoutouts to daniel because turns out the app was screaming at me for having it in a different package.

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.project1.database.AppDatabase
import com.example.project1.database.UserDao
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch

//Like javaFX, any activities I make need to extend what's called ComponentActivity() because it needs to invoke a constructor of ComponentActivity.
//this concept of extending a constructor in a class is new. i will do more learning
class LoginActivity : ComponentActivity() {
    /*
    This activity is for Issue 1. It will help render the login page with 2 fields and 2 buttons (login and create account)
    Disclosure: I used ai to learn kotlin syntax, departing from my experience in java. comments/annotations I leave are largely for my learning and feature takeaways.
     */
    //turns out these vars aren't necessary to running the activity and it was just redundant and messy imports and unneccessary stuff
    //most of this is copied from the main activity kotlin example that was generated
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        setContent {
            Project1Theme {
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { innerPadding ->
                    //i found out that my composable function must be inside column in order for paddings to apply.
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Log in/Create an Account") //,Modifier, Color.Unspecified, TextUnit(20.em)
                        LoginScreen(
                            userDao = userDao,
                            snackbarHostState = snackbarHostState
                        ) // all functions in column .. will get sequentially added to the vbox
                        // in a sense this is reminiscent of the command pattern from software design!
                    }
                }
            }
        }
    }
}

@Composable //this is a marker for methods that describe.. composable class ui elements.
// somehow methods can be defined outside of classes like python!
fun LoginScreen(
    userDao: UserDao,
    snackbarHostState: SnackbarHostState
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

        Button(
            onClick = {
                val enteredUsername = username.trim()

                coroutineScope.launch {
                    if (enteredUsername.isBlank() || password.isBlank()) {
                        snackbarHostState.showSnackbar(
                            "Enter both a username and password."
                        )
                        return@launch
                    }

                    val user = userDao.searchUserVerify(
                        enteredUsername,
                        password
                    )

                    if (user != null) {
                        UserSession.saveUserId(context, user.userId)

                        context.startActivity(
                            Intent(context, MainActivity::class.java)
                        )
                        (context as? Activity)?.finish()
                    } else {
                        snackbarHostState.showSnackbar(
                            "Invalid username or password."
                        )
                    }
                }
            }
        ) {
            Text("Log in")
        }

        Text("Don't have an account?")

        Button(
            onClick = {
                context.startActivity(
                    Intent(context, CreateAccActivity::class.java)
                )
            }
        ) {
            Text("Create account")
        }
    }
}