package com.example.fxinsight.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fxinsight.Greeting
import com.example.fxinsight.R
import com.example.fxinsight.ui.theme.FXInsightTheme


@Composable
fun WelcomeScreen(modifier: Modifier = Modifier,
                  navToNext: () -> Unit)
{
    Column(modifier = Modifier.fillMaxSize().padding(top = 100.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {

            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Welcome Icon",
                modifier = Modifier.fillMaxWidth()
                    .size(400.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Surface()
            {
                Text("FX Insight")

            }
        Surface()
        {
            Text("Clear Fx rates, fast conversions")
        }
       Button(
           onClick = navToNext,  // viewmodel function that check logged in or not
           modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth()

       )
       {
        Text("Start ")
       }

    }

}

@Composable
@Preview
fun WelcomeScreenPreview()
{
    FXInsightTheme {
//        WelcomeScreen()
    }
}