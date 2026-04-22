package com.example.fxinsight.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fxinsight.data.datasource.availableCurrencyList
import com.example.fxinsight.data.network.dto.alert.DirectionStatus
import com.example.fxinsight.data.network.dto.alert.response.AlertResponse
import com.example.fxinsight.ui.viewmodel.AlertViewModel
import com.example.fxinsight.ui.viewmodel.HomeViewModel

@Composable
fun AlertScreen(
    viewModel: AlertViewModel,
    homeViewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B0B1E), Color(0xFF1A1A3A), Color(0xFF2D1B4D))
                )
            )
    ) {
        // Decorative background glow
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp, y = 100.dp)
                .size(350.dp)
                .blur(120.dp)
                .background(Color(0xFF00FFC2).copy(alpha = 0.1f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = Color(0xFF00FFC2),
                    contentColor = Color(0xFF0B0B1E),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Alert")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                AlertHeader(
                    onBack = onBack,
                    onDeleteAll = { viewModel.deleteAllAlerts() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.alerts.isEmpty()) {
                    EmptyAlertsState()
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(uiState.alerts) { alert ->
                            AlertHitCard(
                                alert = alert,
                                onDelete = { viewModel.deleteAlert(alert.id) }
                            )
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            SearchableCreateAlertDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { base, target, price, direction ->
                    viewModel.createAlert(base, target, price, direction)
                    showCreateDialog = false
                },
                base = homeUiState.userInput.baseCurrency,
                target = homeUiState.userInput.targetCurrency,
                rate = homeUiState.conversionRate,
                fetchAlert = viewModel::fetchAlerts

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableCreateAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, DirectionStatus) -> Unit,
    base: String,
    target: String,
    rate: Double,
    fetchAlert: () -> Unit,
) {
    var baseCurrency by remember { mutableStateOf(base) }
    var targetCurrency by remember { mutableStateOf(target) }
    var targetPrice by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf(DirectionStatus.ABOVE) }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectingFor by remember { mutableStateOf("base") } // "base" or "target"

    val filteredList = remember(searchQuery) {
        availableCurrencyList.filter { 
            it.code.contains(searchQuery, ignoreCase = true) || 
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1A1A3A),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "New Price Alert",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.Light)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Selection Summary / Tabs
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                            .background(if(selectingFor == "base") Color(0xFF00FFC2).copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { selectingFor = "base"; searchQuery = "" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Base: $baseCurrency", color = if(selectingFor == "base") Color(0xFF00FFC2) else Color.White.copy(alpha = 0.5f))
                    }
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                            .background(if(selectingFor == "target") Color(0xFF00FFC2).copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { selectingFor = "target"; searchQuery = "" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Target: $targetCurrency", color = if(selectingFor == "target") Color(0xFF00FFC2) else Color.White.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search currency...", color = Color.White.copy(alpha = 0.3f)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF00FFC2)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00FFC2),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                    ),
                    singleLine = true
                )

                // List
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredList) { currency ->
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                .clickable { 
                                    if(selectingFor == "base") {
                                        baseCurrency = currency.code
                                        selectingFor = "target"
                                        searchQuery = ""
                                    } else {
                                        targetCurrency = currency.code
                                    }
                                }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currency.code, color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(12.dp))
                                Text(currency.name, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                        }
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current Rate",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = rate.toString(), // dynamic
                        color = Color(0xFF00FFC2),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                    Row(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).padding(4.dp)
                    ) {
                        DirectionToggleItem("Above", direction == DirectionStatus.ABOVE, Modifier.weight(1f)) { direction = DirectionStatus.ABOVE }
                        DirectionToggleItem("Below", direction == DirectionStatus.BELOW, Modifier.weight(1f)) { direction = DirectionStatus.BELOW }
                    }
                    Spacer(Modifier.width(12.dp))
                    OutlinedTextField(
                        value = targetPrice,
                        onValueChange = { targetPrice = it },
                        placeholder = { Text("Price", color = Color.White.copy(alpha = 0.3f)) },
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00FFC2),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))



                Button(
                    onClick = { 
                        val price = targetPrice.toDoubleOrNull() ?: 0.0
                        onConfirm(baseCurrency, targetCurrency, price, direction)
                        fetchAlert()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFC2), contentColor = Color(0xFF0B0B1E))
                ) {
                    Text("Create Alert", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun DirectionToggleItem(label: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF00FFC2).copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF00FFC2) else Color.White.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun AlertHeader(onBack: () -> Unit, onDeleteAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Price Alerts",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
            )
        }

        IconButton(
            onClick = onDeleteAll,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Red.copy(alpha = 0.1f))
        ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = "Delete All", tint = Color.Red.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun AlertHitCard(
    alert: AlertResponse,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                0.5.dp,
                if (alert.isTriggered) Color(0xFF00FFC2).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status Indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (alert.isTriggered) Color(0xFF00FFC2) else Color.White.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${alert.baseCurrency} → ${alert.targetCurrency}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Divider(color = Color.White.copy(alpha = 0.05f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Target Price",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.4f))
                    )
                    Text(
                        text = "${if (alert.direction == DirectionStatus.ABOVE) "Above" else "Below"} ${alert.alertTarget}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Last Rate",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.4f))
                    )
                    Text(
                        text = alert.lastCheckedRate.toString(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (alert.isTriggered) Color(0xFF00FFC2) else Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }

            if (alert.isTriggered) {
                Surface(
                    color = Color(0xFF00FFC2).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            null,
                            tint = Color(0xFF00FFC2),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Target hit! Price is currently favorable.",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FFC2))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAlertsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.NotificationsNone,
            null,
            modifier = Modifier.size(64.dp),
            tint = Color.White.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No active alerts",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.3f))
        )
    }
}
