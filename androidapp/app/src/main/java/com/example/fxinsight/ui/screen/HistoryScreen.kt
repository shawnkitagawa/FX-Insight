package com.example.fxinsight.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fxinsight.ui.theme.FXInsightTheme
import com.example.fxinsight.R


@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(text = "Conversion history")


        val dummyList: List<String> = listOf("1", "2", "3")
        var filter by remember { mutableStateOf("All") }
        // filter must be a state!
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly)
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
        record(dummyList)
    }

}

@Composable
fun record(
    recordList: List<String>,  // change this data type later
)
{
    LazyColumn()
    {
        items(3) { record ->

            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Currency Flag "

                )
                Text(text = "JPY -> USD")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = 1234.toString())

            }

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