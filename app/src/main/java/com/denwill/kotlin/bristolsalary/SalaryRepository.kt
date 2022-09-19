package com.denwill.kotlin.bristolsalary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

enum class FilterMode { ALL, OUTSTANDING, COMPLETED }
class SalaryRepository(
    private val store: SalaryEntity.Store,
    private val appScope: CoroutineScope){
    fun items(filterMode: FilterMode = FilterMode.ALL): Flow<List<SalaryModel>> =
        filteredEntities(filterMode).map { all -> all.map { it.toModel() } }

    private fun filteredEntities(filterMode: FilterMode) = when (filterMode) {
        FilterMode.ALL -> store.all()
        else -> store.all()
        //FilterMode.OUTSTANDING -> store.filtered(isCompleted = false)
        //FilterMode.COMPLETED -> store.filtered(isCompleted = true)
    }

    fun find(id: String?): Flow<SalaryModel?> = store.find(id).map { it?.toModel() }

    suspend fun save(model: SalaryModel) {
        withContext(appScope.coroutineContext) {
            store.save(SalaryEntity(model))
        }
    }

    suspend fun delete(model: SalaryModel) {
        withContext(appScope.coroutineContext) {
            store.delete(SalaryEntity(model))
        }
    }

    /*suspend fun importItems(url: String) {
        withContext(appScope.coroutineContext) {
            store.importItems(remoteDataSource.load(url).map { it.toEntity() })
        }
    }*/
}