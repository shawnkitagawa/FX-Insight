package com.example.fxinsight.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fxinsight.ui.theme.FXInsightTheme


@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {

    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(text = "Conversion history")

        var filter by remember { mutableStateOf("All") }
        // filter must be a state!
        Row()
        {
            FilterChip(
                selected = filter == "All",
                onClick = {filter = "All"},
                label = {Text("All")}

            )
            FilterChip(
                selected = filter == "Today",
                onClick = {filter = "Today"},
                label = {Text("Today")}

            )
            FilterChip(
                selected = filter == "Week",
                onClick = {filter = "Week"},
                label = {Text("Week")}
            )
        }
    }

}

@Composable
@Preview
fun HistoryScreenPreview()
{
    FXInsightTheme {
//        WelcomeScreen()
        HistoryScreen()
    }
}