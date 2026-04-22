package com.example.fxinsight.ui.screen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingFlat
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fxinsight.data.datasource.CurrencyItemData
import com.example.fxinsight.data.datasource.availableCurrencyList
import com.example.fxinsight.data.network.dto.alert.DirectionStatus
import com.example.fxinsight.data.network.dto.alert.response.AlertResponse
import com.example.fxinsight.data.network.dto.favorite.response.FavoriteResponse
import com.example.fxinsight.ui.theme.FXInsightTheme
import com.example.fxinsight.ui.uistate.FavUiState
import com.example.fxinsight.ui.uistate.HomeUiState
import com.example.fxinsight.ui.uistate.UiState
import com.example.fxinsight.ui.viewmodel.AlertViewModel
import com.example.fxinsight.ui.viewmodel.FavoriteViewModel
import com.example.fxinsight.ui.viewmodel.HomeViewModel
import androidx.compose.ui.platform.LocalClipboardManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(),
    favViewModel: FavoriteViewModel = viewModel(),
    alertViewModel: AlertViewModel = viewModel(factory = AlertViewModel.Factory),
    onNavToAlerts: () -> Unit = {}
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val favoriteUiState by favViewModel.uiState.collectAsState()
    val alertUiState by alertViewModel.uiState.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val baseCurrency = homeUiState.userInput.baseCurrency
    val targetCurrency = homeUiState.userInput.targetCurrency
    val convertedAmount = homeUiState.convertedAmount

    var baseAmountText by rememberSaveable { mutableStateOf(homeUiState.userInput.baseAmount.toString()) }
    var convertedAmountText by rememberSaveable { mutableStateOf(homeUiState.convertedAmount.toString()) }

    val triggeredAlert = alertUiState.alerts.firstOrNull {
        it.isTriggered &&
                it.baseCurrency == baseCurrency &&
                it.targetCurrency == targetCurrency
    }

    var dismissedAlertKey by rememberSaveable { mutableStateOf<String?>(null) }

    val currentAlertKey = triggeredAlert?.let {
        "${it.baseCurrency}_${it.targetCurrency}_${it.alertTarget}_${it.lastCheckedRate}_${it.direction}"
    }

    val shouldShowTriggeredAlert =
        triggeredAlert != null && dismissedAlertKey != currentAlertKey


    LaunchedEffect(
        homeUiState.userInput.baseCurrency,
        homeUiState.userInput.targetCurrency
    ) {
        baseAmountText = homeViewModel.formatCurrencyDisplay(homeUiState.userInput.baseAmount)
        convertedAmountText = homeViewModel.formatCurrencyDisplay(homeUiState.convertedAmount)
    }

    LaunchedEffect(homeUiState.convertedAmount) {
        convertedAmountText = homeViewModel.formatCurrencyDisplay(homeUiState.convertedAmount)
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
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-50).dp)
                .size(300.dp)
                .blur(100.dp)
                .background(Color(0xFF4A90E2).copy(alpha = 0.15f), CircleShape)
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DashboardHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // Triggered Alert Highlight (Filtered by CURRENT base and target)
            val triggeredAlert = alertUiState.alerts.firstOrNull { 
                it.isTriggered && 
                it.baseCurrency == baseCurrency && 
                it.targetCurrency == targetCurrency 
            }

            AnimatedVisibility(
                visible = shouldShowTriggeredAlert,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                triggeredAlert?.let {
                    TriggeredAlertSection(
                        baseCurrency = it.baseCurrency,
                        targetCurrency = it.targetCurrency,
                        direction = it.direction,
                        alertTarget = it.alertTarget,
                        rate = it.lastCheckedRate,
                        onViewAllClick = onNavToAlerts,
                        onDismiss = {
                            dismissedAlertKey = currentAlertKey
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Exchange Rate Main Card
            ExchangeRateMainCard(
                baseCurrency = baseCurrency,
                targetCurrency = targetCurrency,
                onSwap = {
                    homeViewModel.swapCurrencies()
                },
                onChangeCurrencies = { showSheet = true },
                updateBaseAmount = { input ->
                    if (input.isEmpty()) {
                        baseAmountText = ""
                        convertedAmountText = ""
                        homeViewModel.updateHomeBaseAmountInput(0.0)
                    } else if (input.matches(Regex("""\d*\.?\d*"""))) {
                        baseAmountText = input
                        input.toDoubleOrNull()?.let { value ->
                            homeViewModel.updateHomeBaseAmountInput(value)
                        }
                    }
                },
                updateConvertedAmount = { input ->
                    if (input.isEmpty() || input.matches(Regex("""\d*\.?\d*"""))) {
                        convertedAmountText = input
                    }
                },
                convertedRate = homeUiState.conversionRate,
                conversionCreatedAt = homeUiState.conversionCreatedAt,
                baseAmountText = baseAmountText,
                convertedAmountText = convertedAmountText,
                formatCurrencyDisplay = homeViewModel::formatCurrencyDisplay
            )

            Spacer(modifier = Modifier.height(16.dp))

            MarketPulseSection(homeUiState)

            Spacer(modifier = Modifier.height(24.dp))

            FavoritesSection(
                favorites = favoriteUiState.favorites.favList,
                onClick = { favorite ->
                    homeViewModel.updateCurrencyPair(favorite.baseCurrency, favorite.targetCurrency)
                },
                onDeleteClick = { favorite ->
                    favViewModel.deleteFavorite(favorite.id)
                },
                onAddClick = { showSheet = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Active Alerts Dashboard Section (Filtered by CURRENT currency pair)
            val currentPairAlerts = alertUiState.alerts.filter { 
                !it.isTriggered && 
                it.baseCurrency == baseCurrency && 
                it.targetCurrency == targetCurrency 
            }
            
            ActiveAlertsDashboardSection(
                alerts = currentPairAlerts,
                onViewAllClick = onNavToAlerts
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showSheet) {
            CurrencySelectionBottomSheet(
                sheetState = sheetState,
                onDismiss = { showSheet = false },
                onSelectionComplete = { base, target ->
                    homeViewModel.updateCurrencyPair(base, target)
                    showSheet = false
                },
                onSaveFavorite = { base, target ->
                    favViewModel.createFavorite(base, target)
                },
                initialBase = baseCurrency,
                initialTarget = targetCurrency,
            )
        }
    }
}

@Composable
fun DashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "FX Insight",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = 2.sp,
                color = Color.White
            )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HeaderIconButton(Icons.Outlined.Notifications)
            HeaderIconButton(Icons.Outlined.Person)
        }
    }
}

@Composable
fun HeaderIconButton(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun ExchangeRateMainCard(
    baseCurrency: String,
    targetCurrency: String,
    baseAmountText: String,
    convertedAmountText: String,
    convertedRate: Double,
    conversionCreatedAt: String,
    onSwap: () -> Unit,
    onChangeCurrencies: () -> Unit,
    updateBaseAmount: (String) -> Unit,
    updateConvertedAmount: (String) -> Unit,
    formatCurrencyDisplay: (Double) -> String
) {
    val clipboardManager = LocalClipboardManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencyInputBox(
                    amount = baseAmountText,
                    onAmountChange = updateBaseAmount,
                    currencyCode = baseCurrency,
                    modifier = Modifier.weight(1f),
                    onCopyClick = {
                        clipboardManager.setText(AnnotatedString(baseAmountText))
                    }
                )

                IconButton(
                    onClick = onSwap,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, "Swap", tint = Color(0xFF00FFC2))
                }

                CurrencyInputBox(
                    amount = convertedAmountText,
                    onAmountChange = updateConvertedAmount,
                    currencyCode = targetCurrency,
                    modifier = Modifier.weight(1f),
                    onCopyClick = {
                        clipboardManager.setText(AnnotatedString(convertedAmountText))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onChangeCurrencies() }
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    null,
                    tint = Color(0xFF00FFC2),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Change currencies",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF00FFC2).copy(alpha = 0.8f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "1 $baseCurrency = ${formatCurrencyDisplay(convertedRate)} $targetCurrency",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
            )
            Text(
                text = "Updated $conversionCreatedAt • Mid-market rate",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}
@Composable
fun MarketPulseSection(uiState: HomeUiState) {
    val dailyChange = uiState.dailyChangeState as? UiState.Success
    val weeklyStats = uiState.weeklyStatisticState as? UiState.Success

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Daily Change",
            value = if (dailyChange != null) {
                formatPercent(dailyChange.data.change)
            } else "...",
            color = if (dailyChange != null && dailyChange.data.change >= 0) Color(0xFF00FFC2) else Color(0xFFFF4B4B)
        )

        WeeklyTrendCard(
            modifier = Modifier.weight(1f),
            state = uiState.weeklyStatisticState
        )
    }
}

@Composable
fun WeeklyTrendCard(
    modifier: Modifier,
    state: UiState<com.example.fxinsight.data.network.dto.currency.response.WeeklyStatisticsResponse>
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Weekly Trend",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            when (state) {
                is UiState.Loading -> Text("...", color = Color.White.copy(alpha = 0.6f))
                is UiState.Error -> Text("Unavailable", color = Color.White.copy(alpha = 0.2f), fontSize = 14.sp)
                is UiState.Success -> {
                    val data = state.data
                    val color = when {
                        data.trend.contains("up", true) -> Color(0xFF00FFC2)
                        data.trend.contains("down", true) -> Color(0xFFFF4B4B)
                        else -> Color.White.copy(alpha = 0.6f)
                    }
                    val icon = when {
                        data.trend.contains("up", true) -> Icons.Outlined.TrendingUp
                        data.trend.contains("down", true) -> Icons.Outlined.TrendingDown
                        else -> Icons.Outlined.TrendingFlat
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = data.trend.uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(color = color, fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = formatPercent(data.changePercent),
                        style = MaterialTheme.typography.labelSmall.copy(color = color.copy(alpha = 0.7f))
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, color: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.4f)))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(color = color, fontWeight = FontWeight.Medium))
        }
    }
}

private fun formatPercent(value: Double): String {
    val prefix = if (value >= 0) "+" else ""
    return "$prefix${"%.2f".format(value)}%"
}

@Composable
fun ActiveAlertsDashboardSection(alerts: List<AlertResponse>, onViewAllClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Active Alerts",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Light)
            )
            TextButton(onClick = onViewAllClick) {
                Text("Manage", color = Color(0xFF00FFC2))
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No pending alerts for this pair", color = Color.White.copy(alpha = 0.3f))
            }
        } else {
            // Show up to 3 alerts on dashboard to keep it tidy
            alerts.take(3).forEach { alert ->
                DashboardAlertCard(alert, onClick = onViewAllClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DashboardAlertCard(alert: AlertResponse, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Notifications,
                null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${alert.baseCurrency} → ${alert.targetCurrency}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${if (alert.direction == DirectionStatus.ABOVE) ">" else "<"} ${alert.alertTarget}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF00FFC2).copy(alpha = 0.8f))
            )
        }
    }
}

@Composable
fun CurrencyInputBox(
    amount: String,
    onAmountChange: (String) -> Unit,
    currencyCode: String,
    modifier: Modifier = Modifier,
    onCopyClick: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currencyCode,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.5f)
                )
            )

            if (onCopyClick != null) {
                IconButton(
                    onClick = onCopyClick,
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy amount",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        BasicTextField(
            value = amount,
            onValueChange = onAmountChange,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier.padding(vertical = 4.dp),
            cursorBrush = Brush.verticalGradient(
                listOf(Color(0xFF00FFC2), Color(0xFF00FFC2))
            ),
            singleLine = true
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.1f))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectionComplete: (String, String) -> Unit,
    onSaveFavorite: (String, String) -> Unit,
    initialBase: String,
    initialTarget: String,
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentStep by remember { mutableStateOf(1) }
    var selectedBase by remember { mutableStateOf(initialBase) }
    var selectedTarget by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val filteredCurrencies = remember(searchQuery) {
        availableCurrencyList.filter {
            it.code.contains(searchQuery, ignoreCase = true) ||
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A1A3A),
        contentColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(horizontal = 24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Select Currencies", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light))
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color(0xFF00FFC2) else Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.05f)).padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                SelectionProgressItem(label = "Base", currency = selectedBase, isActive = currentStep == 1, onClick = { currentStep = 1 })
                Icon(Icons.Default.ArrowForward, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(16.dp))
                SelectionProgressItem(
                    label = "Target",
                    currency = if (selectedTarget.isBlank()) "Select" else selectedTarget,
                    isActive = currentStep == 2,
                    onClick = { if (currentStep != 1) { currentStep = 2 } }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                placeholder = { Text("Search code or name", color = Color.White.copy(alpha = 0.3f)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF00FFC2)) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF00FFC2), unfocusedBorderColor = Color.White.copy(alpha = 0.1f), cursorColor = Color(0xFF00FFC2)),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredCurrencies) { currency ->
                    val isSelected = if (currentStep == 1) selectedBase == currency.code else selectedTarget == currency.code
                    val isDisabled = (currentStep == 1 && selectedTarget == currency.code) || (currentStep == 2 && selectedBase == currency.code)

                    CurrencySelectionItem(currency = currency, isSelected = isSelected, isDisabled = isDisabled, onClick = {
                        if (!isDisabled) {
                            if (currentStep == 1) {
                                selectedBase = currency.code
                                currentStep = 2
                                searchQuery = ""
                            } else {
                                selectedTarget = currency.code
                                onSelectionComplete(selectedBase, selectedTarget)
                                if (isFavorite) {
                                    onSaveFavorite(selectedBase, selectedTarget)
                                    isFavorite = false
                                }
                            }
                        }
                    })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SelectionProgressItem(label: String, currency: String, isActive: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = if (isActive) Color(0xFF00FFC2) else Color.White.copy(alpha = 0.3f)))
        Text(text = currency, style = MaterialTheme.typography.titleMedium.copy(color = if (isActive) Color(0xFF00FFC2) else Color.White.copy(alpha = 0.6f), fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal))
        if (isActive) { Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF00FFC2))) }
    }
}

@Composable
fun CurrencySelectionItem(currency: CurrencyItemData, isSelected: Boolean, isDisabled: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (isSelected) Color(0xFF00FFC2).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f)).border(width = 1.dp, color = if (isSelected) Color(0xFF00FFC2).copy(alpha = 0.3f) else Color.Transparent, shape = RoundedCornerShape(16.dp)).clickable(enabled = !isDisabled) { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)), contentAlignment = Alignment.Center) {
            Text(text = currency.code.take(2), style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.4f)))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = currency.code, style = MaterialTheme.typography.titleMedium.copy(color = if (isDisabled) Color.White.copy(alpha = 0.2f) else Color.White))
            Text(text = currency.name, style = MaterialTheme.typography.bodySmall.copy(color = if (isDisabled) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.4f)))
        }
        if (isSelected) { Icon(Icons.Default.Check, null, tint = Color(0xFF00FFC2), modifier = Modifier.size(20.dp)) }
    }
}

@Composable
fun FavoritesSection(
    favorites: List<FavoriteResponse>,
    onClick: (FavoriteResponse) -> Unit,
    onDeleteClick: (FavoriteResponse) -> Unit,
    onAddClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Favorites",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Light
                )
            )
            TextButton(onClick = onAddClick) {
                Text("+ Add", color = Color(0xFF00FFC2))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(favorites) { favorite ->
                FavoriteItem(
                    favorite = favorite,
                    onClick = { onClick(favorite) },
                    onDeleteClick = { onDeleteClick(favorite) }
                )
            }
        }
    }
}

@Composable
fun FavoriteItem(
    favorite: FavoriteResponse,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                0.5.dp,
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${favorite.baseCurrency} → ${favorite.targetCurrency}",
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.10f),
                    shape = CircleShape
                )
                .clickable(onClick = onDeleteClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete favorite",
                tint = Color.White.copy(alpha = 0.65f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun TriggeredAlertSection(
    modifier: Modifier = Modifier,
    baseCurrency: String,
    targetCurrency: String,
    direction: DirectionStatus,
    alertTarget: Double,
    rate: Double,
    onViewAllClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Alert Triggered",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Light
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onViewAllClick) {
                    Text("View All", color = Color(0xFF00FFC2))
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss alert",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF00FFC2).copy(alpha = 0.05f))
                .border(
                    1.dp,
                    Color(0xFF00FFC2).copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
                .clickable { onViewAllClick() }
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$baseCurrency → $targetCurrency",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00FFC2).copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "HIT",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF00FFC2))
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Target",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        Text(
                            text = "${direction.name} $alertTarget",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        Text(
                            text = rate.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF00FFC2))
                        )
                    }
                }
            }
        }
    }
}