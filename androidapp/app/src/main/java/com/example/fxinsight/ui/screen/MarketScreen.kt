package com.example.fxinsight.ui.screen

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fxinsight.data.datasource.availableCurrencyList
import com.example.fxinsight.data.network.dto.currency.TimeGroup
import com.example.fxinsight.data.network.dto.currency.response.WeeklyStatisticsResponse
import com.example.fxinsight.ui.theme.FXInsightTheme
import com.example.fxinsight.ui.uistate.Graph
import com.example.fxinsight.ui.uistate.MarketState
import com.example.fxinsight.ui.uistate.UiState
import com.example.fxinsight.ui.viewmodel.HomeViewModel
import com.example.fxinsight.ui.viewmodel.MarketViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MarketScreen(
    homeViewModel: HomeViewModel = viewModel(),
    marketViewModel: MarketViewModel = viewModel()
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val marketUiState by marketViewModel.uiState.collectAsState()

    val baseCurrency = homeUiState.userInput.baseCurrency ?: availableCurrencyList[0].code
    val targetCurrency = homeUiState.userInput.targetCurrency ?: availableCurrencyList[1].code

    LaunchedEffect(baseCurrency, targetCurrency) {
        marketViewModel.getAiInsight(baseCurrency, targetCurrency)
        marketViewModel.ResetGraph()
        marketViewModel.ResetMarketState()
    }

    LaunchedEffect(marketUiState.graphs) {
        if (marketUiState.graphs.isEmpty()) {
            marketViewModel.getMarketData(baseCurrency, targetCurrency)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0B1E), Color(0xFF1A1A3A), Color(0xFF2D1B4D))
                )
            )
    ) {
        // Background Glow
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = 100.dp)
                .size(300.dp)
                .blur(100.dp)
                .background(Color(0xFF4A90E2).copy(alpha = 0.1f), CircleShape)
        )

        Crossfade(targetState = marketUiState.marketState, label = "MarketState") { state ->
            when (state) {
                is MarketState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00FFC2))
                    }
                }
                is MarketState.Error -> {
                    MarketErrorView(message = state.message) {
                        marketViewModel.getMarketData(baseCurrency, targetCurrency)
                    }
                }
                else -> {
                    MarketContent(
                        marketUiState = marketUiState,
                        homeUiState = homeUiState,
                        marketViewModel = marketViewModel,
                        formatCurrencyDisplay = { value -> homeViewModel.formatCurrencyDisplay(value)}
                    )
                }
            }
        }
    }
}

@Composable
fun MarketContent(
    marketUiState: com.example.fxinsight.ui.uistate.MarketUiState,
    homeUiState: com.example.fxinsight.ui.uistate.HomeUiState,
    marketViewModel: MarketViewModel,
    formatCurrencyDisplay: (Double) -> String
) {
    val uiState by marketViewModel.uiState.collectAsState()
    val baseCurrency = homeUiState.userInput.baseCurrency ?: availableCurrencyList[0].code
    val targetCurrency = homeUiState.userInput.targetCurrency ?: availableCurrencyList[1].code

    val aiInsight = when(uiState.InsightState)
    {
        is MarketState.Idle -> "AI insight will appear here"
        is MarketState.Loading -> "Loading AI Insight"
        is MarketState.Error -> "Error Loading AI Insight"
        else -> uiState.Insight?: ""

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Market Analysis",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 2.sp,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        MarketChartSection(
            selectedGraph = marketUiState.graphs,
            selectRange = marketUiState.selectRange,
            baseCurrency = baseCurrency,
            targetCurrency = targetCurrency,
            rate = homeUiState.conversionRate,
            formatCurrencyDisplay = formatCurrencyDisplay
        )

        Spacer(modifier = Modifier.height(24.dp))

        MarketFilterTabs(
            updateMarketSelect = { filter -> marketViewModel.updateTimeGroup(filter) },
            baseCurrency = baseCurrency,
            targetCurrency = targetCurrency,
            getMarketData = { base, target -> marketViewModel.getMarketData(base, target) },
            marketTarget = marketUiState.selectRange
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Added Market Statistics Section
        if (homeUiState.weeklyStatisticState is UiState.Success) {
            MarketStatisticsSection(stats = (homeUiState.weeklyStatisticState as UiState.Success).data,
                formatCurrencyDisplay = formatCurrencyDisplay)
            Spacer(modifier = Modifier.height(32.dp))
        }

        MarketAiInsightSection(aiInsight = aiInsight)
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun MarketStatisticsSection(
    stats: WeeklyStatisticsResponse,
    modifier: Modifier = Modifier,
    formatCurrencyDisplay: (Double) -> String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = "Weekly Statistics",
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF00FFC2),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            StatItem(label = "High",
                value = stats.max,
                modifier = Modifier.weight(1f),
                formatCurrencyDisplay = formatCurrencyDisplay)
            StatItem(label = "Low",
                value = stats.min,
                modifier = Modifier.weight(1f),
                formatCurrencyDisplay = formatCurrencyDisplay)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            StatItem(label = "Average",
                value = stats.average,
                modifier = Modifier.weight(1f),
                formatCurrencyDisplay = formatCurrencyDisplay)
            StatItem(label = "Median",
                value = stats.median,
                modifier = Modifier.weight(1f),
                formatCurrencyDisplay = formatCurrencyDisplay)
        }
    }
}

@Composable
private fun StatItem(label: String, value: Double,
                     modifier: Modifier = Modifier,
                     formatCurrencyDisplay: (Double) -> String
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.4f),
                fontWeight = FontWeight.Normal
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatCurrencyDisplay(value),
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun MarketErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, null, tint = Color.Red.copy(0.5f), modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(message, color = Color.White.copy(0.7f), textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))
        ) {
            Text("Retry", color = Color.White)
        }
    }
}

@Composable
fun MarketChartSection(
    selectedGraph: Map<TimeGroup, Graph>,
    selectRange: TimeGroup,
    baseCurrency: String,
    targetCurrency: String,
    rate: Double,
    formatCurrencyDisplay: (Double) -> String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$baseCurrency / $targetCurrency",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )
                    Text(
                        text = formatCurrencyDisplay(rate),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color(0xFF00FFC2),
                            fontWeight = FontWeight.Light
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF00FFC2).copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MarketChart(selectedGraph = selectedGraph, selectRange = selectRange)
        }
    }
}

@Composable
fun MarketChart(selectedGraph: Map<TimeGroup, Graph>, selectRange: TimeGroup) {
    val graph = selectedGraph[selectRange]
    val xValues = graph?.x?.map { it.toDouble() } ?: emptyList()
    val yValues = graph?.y ?: emptyList()

    val modelProducer = remember { CartesianChartModelProducer() }
    val scrollState = rememberVicoScrollState()

    if (xValues.isEmpty() || yValues.isEmpty() || xValues.size != yValues.size) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No chart data available", color = Color.White.copy(0.2f))
        }
        return
    }

    val minY = yValues.minOrNull() ?: 0.0
    val maxY = yValues.maxOrNull() ?: 0.0
    val diff = maxY - minY
    val padding = if (diff == 0.0) 0.0001 else diff * 0.2

    val bottomFormatter = remember(selectRange) {
        CartesianValueFormatter { _, value, _ ->
            val timestamp = value.toLong() * 1000
            val pattern = when (selectRange) {
                TimeGroup.DAY -> "MM/dd"
                else -> "MM/dd"
            }
            SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))
        }
    }

    LaunchedEffect(xValues, yValues) {
        modelProducer.runTransaction {
            lineSeries {
                series(x = xValues, y = yValues)
            }
        }
    }

    val zoomState = rememberVicoZoomState(
        initialZoom = Zoom.Content,
        minZoom = Zoom.Content,
        maxZoom = Zoom.x(6.0)
    )

    val step = ((maxY - minY) / 4).coerceAtLeast(0.00001)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minY = minY - padding,
                    maxY = maxY + padding,
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                label = TextComponent(
                    color = Color.White.copy(alpha = 0.7f).toArgb(),
                    textSizeSp = 12f,
                ),
                itemPlacer = VerticalAxis.ItemPlacer.step(step = { step }),
                valueFormatter = { _, value, _ ->
                    when {
                        value > 100 -> "%.2f".format(value)
                        value > 1 -> "%.3f".format(value)
                        else -> "%.5f".format(value)
                    }
                }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = TextComponent(
                    color = Color.White.copy(alpha = 0.7f).toArgb(),
                    textSizeSp = 12f,
                ),
                valueFormatter = bottomFormatter,
                itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 6 }) }
            )
        ),
        modelProducer = modelProducer,
        zoomState = zoomState,
        scrollState = scrollState,
        modifier = Modifier.fillMaxWidth().height(200.dp),
    )
}

@Composable
fun MarketFilterTabs(
    updateMarketSelect: (TimeGroup) -> Unit,
    baseCurrency: String,
    targetCurrency: String,
    getMarketData: (String, String) -> Unit,
    marketTarget: TimeGroup
) {
    val filters = TimeGroup.values().toList()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = marketTarget == filter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f))
                    .clickable {
                        updateMarketSelect(filter)
                        getMarketData(baseCurrency, targetCurrency)
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when(filter) {
                        TimeGroup.DAY -> "1M"
                        TimeGroup.WEEK -> "6M"
                        TimeGroup.MONTH -> "1Y"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}

@Composable
fun MarketAiInsightSection(
    aiInsight: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4A90E2).copy(alpha = 0.1f), Color(0xFFB06AB3).copy(alpha = 0.1f))
                )
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF00FFC2),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Insights",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text =  aiInsight,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 22.sp
                )
            )
        }
    }
}
