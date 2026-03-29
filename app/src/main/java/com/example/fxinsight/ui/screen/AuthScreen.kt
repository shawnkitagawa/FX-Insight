package com.example.fxinsight.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fxinsight.R
import com.example.fxinsight.ui.theme.FXInsightTheme


//Auth state
// login ,
// SIgn up!!!!!!!!!!
@Composable
fun AuthScreen(modifier: Modifier = Modifier,
               navToNext: () -> Unit)
{
    // when the Auth State is login show the login
    // If the Auth State is sign up then we show sign up
    // and add a username and modify the Signin composable
    // both using the same composable
    Signin(navToNext = navToNext)
}

@Composable
fun Signin(modifier: Modifier = Modifier,
           navToNext: () -> Unit )
{
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "auth image"
            )
            Text("FX insight")
            Text("Login to FX Insight Account")

        }
        var text by remember { mutableStateOf("") }

        Column(modifier = Modifier.padding(horizontal = 24.dp))
        {
            Text("Email Address")
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("you@example.com") },
                modifier = Modifier.fillMaxWidth().clip(CircleShape).height(55.dp)

            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Password")
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("At least 8 characters") },
                modifier = Modifier.fillMaxWidth().clip(CircleShape).height(55.dp)
            )
            Spacer(modifier = Modifier.height(16.dp).height(55.dp))
            Button(
                onClick = navToNext, // check if the auhtentication passed or not update the state
                modifier = Modifier.fillMaxWidth()

            )
            {
                Text("Create")
            }
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
            {
                Text("Don't have an account?")
                Spacer(modifier.width(8.dp))
                Text(text = "Sign up",
                    modifier = Modifier.clickable(
                        onClick = {} // state to check sign up or login to toggle this composable
                    ))
            }
        }
    }
}


@Composable
fun SignUp()
{

}



@Composable
@Preview
fun AuthScreenPreview()
{
    FXInsightTheme {

    }
}