package com.example.fxinsight.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fxinsight.Greeting
import com.example.fxinsight.R
import com.example.fxinsight.ui.theme.FXInsightTheme

@Composable
fun DashboardScreen(modifier: Modifier = Modifier)
{
    Column(modifier = modifier.padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {
        val list: List<String> = listOf("JPY", "USD", "EUR")
        DashboardTopBar()
        Spacer(modifier = Modifier.height(64.dp))
        DashboardBody()
        Spacer(modifier = Modifier.height(32.dp))
        FavoritesRow(favorites = list, {}, {})




    }


}
@Composable
fun DashboardTopBar() {
    var amount1 by remember { mutableStateOf("500") }
    var amount2 by remember { mutableStateOf("586.75") }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(150.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencyBox(
                    amount = amount1,
                    onAmountChange = { amount1 = it },
                    currencyCode = "GBP",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        val temp = amount1
                        amount1 = amount2
                        amount2 = temp
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap currencies"
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                CurrencyBox(
                    amount = amount2,
                    onAmountChange = { amount2 = it },
                    currencyCode = "USD",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1 GBP = 1.17350 EUR",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Mid-market exchange rate at 13:35 UTC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun CurrencyBox(
    amount: String,
    onAmountChange: (String) -> Unit,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = amount,
                onValueChange = onAmountChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Currency flag",
                modifier = Modifier.size(10.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = currencyCode,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select currency"
            )
        }
    }
}

@Composable
fun DashboardBody(modifier: Modifier = Modifier)
{
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally)
    {
        Row(modifier = Modifier.height(130.dp))
        {
            dashboardBodyCard(modifier = Modifier.weight(1f),title = "Change", data = 1.6)
            Spacer(modifier = Modifier.width(16.dp))
            dashboardBodyCard(modifier = Modifier.weight(1f),title = "Trend",data = 1234)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.height(130.dp))
        {
            dashboardBodyCard(modifier = Modifier.weight(1f), title = "Range",data = 1234)
            Spacer(modifier = Modifier.width(16.dp))
            dashboardBodyCard(modifier = Modifier.weight(1f), title = "Alert",data = 1234)
        }
    }
}

@Composable
fun dashboardBodyCard(modifier: Modifier = Modifier,
                      title: String,
                      data: Number,

)
{
    Card(modifier = modifier.height(130.dp))
    {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(text = title)
            Text(text = data.toString())
        }

    }

}

@Composable
fun FavoritesRow(
    favorites: List<String>,
    onClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Favorites", style = MaterialTheme.typography.titleMedium)

            TextButton(onClick = onAddClick) {
                Text("+ Add")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { pair ->
                AssistChip(
                    onClick = { onClick(pair) },
                    label = { Text(pair) }
                )
            }
        }
    }
}

@Composable
@Preview
fun DashboardScreenPreview()
{
    FXInsightTheme {
        DashboardScreen()
    }

}