package FoodPicker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.example.project1.Greeting
import com.example.project1.ui.theme.Project1Theme
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em

//Like javaFX, any activities I make need to extend what's called ComponentActivity() because it needs to invoke a constructor of ComponentActivity.
//this concept of extending a constructor in a class is new. i will do more learning
class LoginActivity : ComponentActivity() {
    /*
    This activity is for Issue 1. It will help render the login page with 2 fields and 2 buttons (login and create account)
    Disclosure: I used ai to learn kotlin syntax, departing from my experience in java. comments/annotations I leave are largely for my learning and feature takeaways.
 */
    private lateinit var UsernameField: EditText // private lateinit (essentially promises to assign a value to this null var later) var(different from val which is a final) attributeName: dataType
    private lateinit var PasswordField: EditText

    private lateinit var loginButton: Button
    private lateinit var createAccButton: Button

    //most of this is copied from the main activity kotlin example that was generated
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Log in/Create an Account") //,Modifier, Color.Unspecified, TextUnit(20.em)
                        Text("Username")

                    }
                }
            }
        }
    }
}
    @Composable
    fun LoginScreen() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Column {
            Text("Account Log In")
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )

            Button(onClick = {
                //check db to see if user/pass exist TOGETHER
            }) {
                Text("Log in")
            }

            Button(onClick = {
                // redirect to acc creation..
            }) {
                Text("Create account")
            }
        }
    }
    //this is a marker for methods that describe.. composable class ui elements. somehow methods can be defined outside of classes like python!
