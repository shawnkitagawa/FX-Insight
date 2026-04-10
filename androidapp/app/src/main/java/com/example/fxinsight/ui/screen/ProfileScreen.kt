package com.example.fxinsight.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ProfileScreen(modifier: Modifier = Modifier,
                  signOut: () -> Unit ) {

    Column()
    {
        Button(
            onClick = signOut
        )
        {
            Text("Sign Out")
        }
    }
}