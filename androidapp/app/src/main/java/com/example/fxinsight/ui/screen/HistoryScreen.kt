package com.example.fxinsight.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fxinsight.data.network.dto.history.response.HistoryResponse
import com.example.fxinsight.ui.theme.FXInsightTheme
import com.example.fxinsight.ui.uistate.HistoryFilter
import com.example.fxinsight.ui.uistate.HistoryState
import com.example.fxinsight.ui.viewmodel.HistoryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B0B1E),
                        Color(0xFF1A1A3A),
                        Color(0xFF2D1B4D)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(350.dp)
                .blur(120.dp)
                .background(
                    Color(0xFFB06AB3).copy(alpha = 0.15f),
                    CircleShape
                )
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Conversion History",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your recent currency lookups",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.45f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Crossfade(
                targetState = uiState.fetchHistoryState,
                label = "HistoryFetchState"
            ) { state ->
                when (state) {
                    is HistoryState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00FFC2))
                        }
                    }

                    is HistoryState.Error -> {
                        HistoryErrorView(
                            message = state.message ?: "Failed to load history",
                            onRetry = { viewModel.fetchHistory() }
                        )
                    }

                    else -> {
                        if (uiState.history.isEmpty()) {
                            EmptyHistoryState()
                        } else {
                            HistoryList(recordList = uiState.history)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryList(recordList: List<HistoryResponse>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(
            items = recordList,
            key = { history -> history.id }
        ) { history ->
            HistoryItem(history = history)
        }
    }
}

@Composable
fun HistoryItem(history: HistoryResponse) {
    val formattedDate = formatHistoryDate(history.createdAt)
    val formattedRate = formatRate(history.rate)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.035f))
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Color(0xFF00FFC2),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${history.baseCurrency} → ${history.targetCurrency}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = "1 ${history.baseCurrency} = $formattedRate ${history.targetCurrency}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF00FFC2).copy(alpha = 0.82f)
                    )
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.36f)
                    )
                )
            }
        }
    }
}

private fun formatHistoryDate(createdAt: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")
        val zonedDateTime = Instant.parse(createdAt).atZone(ZoneId.systemDefault())
        formatter.format(zonedDateTime)
    } catch (e: Exception) {
        createdAt
    }
}

private fun formatRate(rate: Double): String {
    return if (rate >= 1000) {
        "%,.2f".format(rate)
    } else {
        "%.4f".format(rate).trimEnd('0').trimEnd('.')
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No conversion history yet",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your recent currency lookups will appear here.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.28f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HistoryErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.Red.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            )
        ) {
            Text("Retry", color = Color.White)
        }
    }
}