package com.example.project1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme
import androidx.compose.ui.unit.sp



@Composable
fun FavoritePage() {

    val myList = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Favorites",
            fontSize = 30.sp
        )

        Button(
            onClick = {
                myList.add((Math.random() * 5 - 1).toString())
            }
        ) {
            Text("Add")
        }

        Text(
            text = myList.toList().toString()
        )
    }
}



@Preview(showBackground = true)
@Composable
fun FavoritePagePreview() {
    Project1Theme {
        FavoritePage()
    }
}
