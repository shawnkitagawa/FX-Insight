package com.example.fxinsight.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.datasource.availableCurrencyList
import com.example.fxinsight.data.network.dto.currency.response.WeeklyStatisticsResponse
import com.example.fxinsight.data.network.dto.history.request.HistoryCreate
import com.example.fxinsight.data.repositiory.CurrencyRepository
import com.example.fxinsight.data.repositiory.FavoriteRepository
import com.example.fxinsight.data.repositiory.HistoryRepository
import com.example.fxinsight.ui.uistate.HomeUiState
import com.example.fxinsight.ui.uistate.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModel(private val currencyRepository: CurrencyRepository,
                    private val historyRepository: HistoryRepository,
                    private val favoriteRepository: FavoriteRepository, ): ViewModel() {


    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        updateCurrencyPair(base = _uiState.value.userInput.baseCurrency,
            target = _uiState.value.userInput.targetCurrency)
    }

    fun swapCurrencies() {
        val currentBase = _uiState.value.userInput.baseCurrency
        val currentTarget = _uiState.value.userInput.targetCurrency
        val currentBaseAmount = _uiState.value.userInput.baseAmount
        val currentConvertedAmount = _uiState.value.convertedAmount
        val currentRate = _uiState.value.conversionRate

        val invertedRate = if (currentRate != 0.0) 1.0 / currentRate else 0.0

        _uiState.value = _uiState.value.copy(
            userInput = _uiState.value.userInput.copy(
                baseCurrency = currentTarget,
                targetCurrency = currentBase,
                baseAmount = currentConvertedAmount
            ),
            convertedAmount = currentBaseAmount,
            conversionRate = invertedRate
        )
        getWeeklyStatistics()
        getDailyChange()
    }

    fun Conversion()
    {
        val baseCurrency = _uiState.value.userInput.baseCurrency.uppercase()
        val targetCurrency = _uiState.value.userInput.targetCurrency.uppercase()
        val baseAmount = _uiState.value.userInput.baseAmount

        if (availableCurrencyList.none{it.code == baseCurrency}||
            availableCurrencyList.none{it.code == targetCurrency})
        {
            _uiState.value = _uiState.value.copy(
                conversionState = UiState.Error(message = "Invalid Currency")
            )
            return
        }

        val request = HistoryCreate(
            baseCurrency = baseCurrency,
            targetCurrency = targetCurrency
        )

        viewModelScope.launch(){
            _uiState.value = _uiState.value.copy(
                conversionState = UiState.Loading
            )
            val results = historyRepository.createHistory(request)

            results.fold(
                onSuccess = { response ->
                    val time = formatHistoryDate(response.createdAt)

                    _uiState.value = _uiState.value.copy(
                        conversionState = UiState.Success(response),
                        conversionRate = response.rate,
                        convertedAmount = baseAmount * response.rate,
                        conversionCreatedAt = time
                    )
                },
                onFailure = {error ->
                    _uiState.value = _uiState.value.copy(
                        conversionState = UiState.Error(message = error.message?: "Conversion failed ")
                    )
                }
            )
        }
    }

    private fun formatHistoryDate(createdAt: String): String {
        return try {
            val instant = Instant.parse(createdAt)

            val zonedDateTime = instant.atZone(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")

            formatter.format(zonedDateTime)
        } catch (e: Exception) {
            createdAt
        }
    }


    fun getWeeklyStatistics()
    {
        Log.d("getWeeklyStatistics", "Start")

        val baseCurrency = _uiState.value.userInput.baseCurrency.uppercase()
        val targetCurrency = _uiState.value.userInput.targetCurrency.uppercase()

        viewModelScope.launch()
        {
            _uiState.value = _uiState.value.copy(
                weeklyStatisticState = UiState.Loading
            )
            val results = currencyRepository.getWeekStatistics(baseCurrency, targetCurrency)

            results.fold(
                onSuccess = { response ->
                    Log.d("getWeeklyStatistics", "Succesful: ${response}")
                    _uiState.value = _uiState.value.copy(
                        weeklyStatisticState = UiState.Success(response)
                    )
                },
                onFailure = {error ->
                    Log.d("getWeeklyStatistics", "error ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        weeklyStatisticState = UiState.Error(message = error.message?: "Weekly statistic fetch failed ")
                    )
                },
            )
        }
    }

    fun getDailyChange()
    {
        val baseCurrency = _uiState.value.userInput.baseCurrency.uppercase()
        val targetCurrency = _uiState.value.userInput.targetCurrency.uppercase()

        viewModelScope.launch{
            _uiState.value = _uiState.value.copy(
                dailyChangeState = UiState.Loading
            )

            val result = currencyRepository.getDailyChange(baseCurrency, targetCurrency)

            result.fold(
                onSuccess = {response ->
                    _uiState.value = _uiState.value.copy(
                        dailyChangeState = UiState.Success(response)
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        dailyChangeState = UiState.Error(message = it.message?: "Daily change fetch failed")
                    )
                }
            )
        }
    }

    fun baseCurrencyInformation()
    {
        val baseCurrency = _uiState.value.userInput.baseCurrency.uppercase()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                baseCurrencyInformationState = UiState.Loading
            )
            val result = currencyRepository.getCurrencyInformation(baseCurrency)
            result.fold(
                onSuccess = {response ->
                    _uiState.value = _uiState.value.copy(
                        baseCurrencyInformationState = UiState.Success(response)
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        baseCurrencyInformationState = UiState.Error(message = it.message?: "Base currency information fetch failed")
                    )
                },
            )
        }
    }

    fun targetCurrencyInformation()
    {
        val targetCurrency = _uiState.value.userInput.targetCurrency.uppercase()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                targetCurrencyInformationState = UiState.Loading
            )
            val result = currencyRepository.getCurrencyInformation(targetCurrency)
            result.fold(
                onSuccess = {response ->
                    _uiState.value = _uiState.value.copy(
                        targetCurrencyInformationState = UiState.Success(response)
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        targetCurrencyInformationState = UiState.Error(message = it.message?: "target currency information fetch failed")
                    )
                },
            )
        }
    }

    fun updateHomeBaseAmountInput(baseAmount: Double)
    {
        _uiState.value = _uiState.value.copy(
            userInput = _uiState.value.userInput.copy(
                baseAmount = baseAmount
            ),
            convertedAmount = baseAmount * _uiState.value.conversionRate
        )
    }

    fun updateHomeConvertedOutput()
    {
        val baseAmount = _uiState.value.userInput.baseAmount
        val rate = _uiState.value.conversionRate
        _uiState.value = _uiState.value.copy(
            convertedAmount = baseAmount * rate
        )
    }

    fun updateCurrencyPair(base: String, target: String) {
        Log.d("updateCurrencyPair", "Triggered")
        _uiState.value = _uiState.value.copy(
            userInput = _uiState.value.userInput.copy(
                baseCurrency = base,
                targetCurrency = target
            )
        )

        // Now state is consistent → safe to refresh
        Conversion()
        getWeeklyStatistics()
        getDailyChange()
    }


    fun formatCurrencyDisplay(value: Double): String {
        val absValue = kotlin.math.abs(value)
        val bd = BigDecimal.valueOf(value)

        val scaled = when {
            absValue >= 1000 -> bd.setScale(2, RoundingMode.HALF_UP)
            absValue >= 1 -> bd.setScale(4, RoundingMode.HALF_UP)
            absValue >= 0.01 -> bd.setScale(6, RoundingMode.HALF_UP)
            absValue >= 0.0001 -> bd.setScale(8, RoundingMode.HALF_UP)
            else -> bd.setScale(10, RoundingMode.HALF_UP)
        }

        return DecimalFormat("#,##0.##########").format(scaled.toDouble())
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val currencyRepository = application.container.currencyRepository
                val historyRepository = application.container.historyRepository
                val favoriteRepository = application.container.favoriteRepository
                HomeViewModel(currencyRepository = currencyRepository,
                    historyRepository = historyRepository,
                    favoriteRepository = favoriteRepository)
            }
        }
    }
}
