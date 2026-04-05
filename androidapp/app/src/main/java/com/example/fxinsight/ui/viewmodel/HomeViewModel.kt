package com.example.fxinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fxinsight.application.FXInsightApplication
import com.example.fxinsight.data.repository.FXInsightRepository

class HomeViewModel(private val repository: FXInsightRepository): ViewModel() {













    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FXInsightApplication
                val repository = application.container.fxInsightRepository
                HomeViewModel(repository)
            }
        }
    }

}