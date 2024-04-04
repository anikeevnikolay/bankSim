package org.example.utils

import org.example.model.BankAccount
import org.example.model.ClientPriority
import org.example.thread.ClientGenerator
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class GracefulShutdownThread(
    private val operators: ExecutorService,
    private val sellers: ExecutorService,
    private val generator: ClientGenerator,
    private val bankAccount: BankAccount,
    private val servicedClients: ConcurrentHashMap<ClientPriority, AtomicLong>,
    private val operatorsLeaved: CountDownLatch
) : Thread() {
    override fun run() {
        log("Банк закрывается")

        generator.interrupt()

        stopOperators.set(true)

        operators.shutdown()
        operators.awaitTermination(10, TimeUnit.SECONDS)

        operatorsLeaved.await()
        stopSellers.set(true)

        sellers.shutdown()
        sellers.awaitTermination(1, TimeUnit.SECONDS)

        log("Банк успешно закрылся. На счету ${bankAccount.money}. Обслужено: $servicedClients")
    }
}