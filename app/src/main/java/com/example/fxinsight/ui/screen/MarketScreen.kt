package com.example.fxinsight.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fxinsight.R
import com.example.fxinsight.ui.theme.FXInsightTheme
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis


@Composable
fun MarketScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        MarketChart()
        MarketFilter()
        MarketAiInsight()
    }


}

@Composable
fun MarketAiInsight()
{
    Text("USD/JPY is showing a mild upward trend over the past 7 days, supported by steady demand for the US dollar. Price movement remains within a tight range, indicating low volatility and no strong breakout signals. In the short term, the pair may continue consolidating unless new economic data triggers momentum.")
}


@Composable
fun MarketFilter()
{
 // The states needs to handle the filter
    var marketTarget by remember { mutableStateOf("1D") }
    Row(horizontalArrangement = Arrangement.SpaceEvenly)
    {
        FilterChip(
            selected =  marketTarget == "1D",
            onClick = {marketTarget = "1D"},
            label = {Text("1D")},
            modifier = Modifier,
        )
        FilterChip(
            selected =  marketTarget == "7D",
            onClick = {marketTarget = "7D"},
            label = {Text("7D")},
            modifier = Modifier,
        )
        FilterChip(
            selected =  marketTarget == "1M",
            onClick = {marketTarget = "1M"},
            label = {Text("1M")},
            modifier = Modifier,
        )
        FilterChip(
            selected =  marketTarget == "6M",
            onClick = {marketTarget = "6M"},
            label = {Text("6M")},
            modifier = Modifier,
        )
        FilterChip(
            selected =  marketTarget == "1Y",
            onClick = {marketTarget = "1Y"},
            label = {Text("1Y")},
            modifier = Modifier,
        )

    }

}

@Composable
fun MarketChart()
{
    val modelProducer = remember { CartesianChartModelProducer()}
    // dummy data
    val dummyRates = listOf(
        149.2f,
        149.5f,
        149.8f,
        150.1f,
        149.7f,
        150.0f,
        150.3f
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = listOf<Int>(0,1,2,3,4,5,6),
                    y = dummyRates
                )
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
    )

}

@Composable
@Preview
fun MarketScreenPreview()
{
    FXInsightTheme {
        MarketScreen()
    }
}