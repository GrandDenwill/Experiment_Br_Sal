package com.denwill.kotlin.bristolsalary

import java.time.Instant
import java.util.*

data class SalaryModel (
    val id:String = UUID.randomUUID().toString(),
    val date:Instant = Instant.now(),
    val revenue:Double,
    val countWorkers:Int = 1,
    val baseSalary:Double = 1993.0,
    val salary:Double =
        ((revenue / countWorkers)/100.toDouble()+baseSalary).let {it - it*0.13 }
)