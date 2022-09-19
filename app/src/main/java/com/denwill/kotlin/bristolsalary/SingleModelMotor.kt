package com.denwill.kotlin.bristolsalary


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SingleModelViewState(
    val item: SalaryModel? = null
)

class SingleModelMotor(
    private val repo: SalaryRepository,
    modelId: String?
) : ViewModel() {
    val states = repo.find(modelId)
        .map { SingleModelViewState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SingleModelViewState())

    fun save(model: SalaryModel) {
        viewModelScope.launch {
            repo.save(model)
        }
    }

    fun delete(model: SalaryModel) {
        viewModelScope.launch {
            repo.delete(model)
        }
    }
}
