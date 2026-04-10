package com.example.fxinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.repositiory.CurrencyRepository
import com.example.fxinsight.data.repository.FXInsightRepository

class MarketViewModel(private val repository: CurrencyRepository): ViewModel() {















    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val repository = application.container.currencyRepository
                MarketViewModel(repository)
            }
        }
    }
}