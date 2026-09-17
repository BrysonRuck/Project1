package com.example.project1
//shoutouts to daniel because turns out the app was screaming at me for having it in a different package.
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import com.example.project1.ui.theme.Project1Theme

//Like javaFX, any activities I make need to extend what's called ComponentActivity() because it needs to invoke a constructor of ComponentActivity.
//this concept of extending a constructor in a class is new. i will do more learning
class LoginActivity : ComponentActivity() {
    /*
    This activity is for Issue 1. It will help render the login page with 2 fields and 2 buttons (login and create account)
    Disclosure: I used ai to learn kotlin syntax, departing from my experience in java. comments/annotations I leave are largely for my learning and feature takeaways.
 */
//    private lateinit var UsernameField: EditText // private lateinit (essentially promises to assign a value to this null var later) var(different from val which is a final) attributeName: dataType
//    private lateinit var PasswordField: EditText

//    private lateinit var loginButton: Button
//    private lateinit var createAccButton: Button
//turns out these vars arenrt necessary to running the activity and it was just redundant and messy imports and unneccessary stuff
    //most of this is copied from the main activity kotlin example that was generated
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //i found out that my composable function must be inside column in order for paddings to apply.
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Text("Log in/Create an Account") //,Modifier, Color.Unspecified, TextUnit(20.em)
                        LoginScreen()// all functions in column .. will get sequentially added to the vbox
                        // in a sense this is reminiscent of the command pattern from software design!
                    }
                }
            }
        }
    }
}
    @Composable     //this is a marker for methods that describe.. composable class ui elements.
    // somehow methods can be defined outside of classes like python!
    fun LoginScreen() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
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
            Button(onClick = {
                // TODO: check db to see if user/pass exist TOGETHER
            }) {
                Text("Log in")
            }
            val context = LocalContext.current
            Text("Don't have an account?")
            Button(onClick = {
                // TODO: redirect to acc creation..
                context.startActivity(Intent(context, CreateAccActivity::class.java))
            }) {
                Text("Create account")
            }
        }
    }
