package org.example.utils

import java.time.LocalTime
import java.util.concurrent.atomic.AtomicBoolean

fun log(msg: String) {
    println("${LocalTime.now()}: $msg")
}

val stopOperators = AtomicBoolean(false)
val stopSellers = AtomicBoolean(false)

val globalSpeedKoef = 10
val globalBankParallelism = 40
val globalSellerParallelism = 4