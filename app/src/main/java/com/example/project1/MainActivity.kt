package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.project1.ui.theme.Project1Theme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            Greeting(
                                name = "Android",
                                onFavoritesClick = {
                                    navController.navigate("favorites")
                                }
                            )
                        }

                        composable("favorites") {
                            FavoritePage()
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun FavoritesPage() {
    Text("Favorites Page")
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    onFavoritesClick: () -> Unit
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

    Button(
        onClick = {
            onFavoritesClick()
        }
    ) {
        Text("Favorites")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Project1Theme {
        Greeting(
            name = "Android",
            onFavoritesClick = {}
        )
    }
}