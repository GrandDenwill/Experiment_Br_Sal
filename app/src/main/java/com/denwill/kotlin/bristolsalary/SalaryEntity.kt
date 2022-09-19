package com.denwill.kotlin.bristolsalary

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.*

@Entity(tableName = "salaries", indices = [Index(value = ["id"])])
data class SalaryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val date: Instant = Instant.now(),
    val revenue: Double,
    val countWorkers: Int,
    val baseSalary: Double,
    val salary: Double
) {
    constructor(model: SalaryModel) : this(
        id = model.id,
        date = model.date,
        revenue = model.revenue,
        countWorkers = model.countWorkers,
        baseSalary = model.baseSalary,
        salary = model.salary
    )

    fun toModel(): SalaryModel {
        return SalaryModel(
            id = id,
            date = date,
            revenue = revenue,
            countWorkers = countWorkers,
            baseSalary = baseSalary,
            salary = salary
        )
    }

    @Dao
    interface Store {
        @Query("SELECT * FROM salaries ORDER BY date")
        fun all(): Flow<List<SalaryEntity>>

        //@Query("SELECT * FROM salaries WHERE isCompleted = :isCompleted ORDER BY description")
        //fun filtered(isCompleted: Boolean): Flow<List<ToDoEntity>>

        @Query("SELECT * FROM salaries WHERE id = :modelId")
        fun find(modelId: String?): Flow<SalaryEntity?>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun save(vararg entities: SalaryEntity)

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun importItems(entities: List<SalaryEntity>)

        @Delete
        suspend fun delete(vararg entities: SalaryEntity)
    }
}