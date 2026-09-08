package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme

class SetPreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Project1Theme {
                var location by remember { mutableStateOf("") }
                var distance by remember { mutableStateOf("") }
                var statusMessage by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Location")

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it }
                    )

                    Text("Distance (in miles) for radius")

                    OutlinedTextField(
                        value = distance,
                        onValueChange = { distance = it }
                    )

                    Button(
                        onClick = {
                            //later: update the current user's database record here.
                            statusMessage = "Your preferences have been updated."
                        }
                    ) {
                        Text("Save preferences")
                    }

                    Text(statusMessage)
                }
            }
        }
    }
}