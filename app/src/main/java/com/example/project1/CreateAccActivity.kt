package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.project1.ui.theme.Project1Theme

class CreateAccActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    //note as of 9/9/26: Victoria made the redirect but there is no logic here yet
    //TODO (later tonight prolly): make the create account front-end
}
@Composable
fun CreateAccPage() {
    Column() { }
}
@Preview(showBackground = true)
@Composable
fun CreateAccPagePreview() {
    Project1Theme {
        FavoritePage()
    }
}