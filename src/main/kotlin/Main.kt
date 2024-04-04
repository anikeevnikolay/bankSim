package org.example

import org.example.model.BankAccount
import org.example.model.BankClient
import org.example.model.ClientPriority
import org.example.sharingobjects.Printer
import org.example.thread.BankOperator
import org.example.thread.ClientGenerator
import org.example.thread.Seller
import org.example.utils.GracefulShutdownThread
import org.example.utils.globalBankParallelism
import org.example.utils.globalSellerParallelism
import java.math.BigDecimal
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong

fun main() {

    val comparator = compareBy<BankClient> { it.priority.priorValue }
        .thenComparing(compareBy { it.id })
    val queue = PriorityBlockingQueue(globalBankParallelism, comparator)
    val storeQueue = ArrayBlockingQueue<BankClient>(100_000)

    val printer = Printer()
    val servicedClients = ConcurrentHashMap<ClientPriority, AtomicLong>()
    val bankAccount = BankAccount(BigDecimal.valueOf(10000))

    val starter = CountDownLatch(globalBankParallelism)
    val operatorsLeaved = CountDownLatch(globalBankParallelism)
    val operators = Executors.newFixedThreadPool(globalBankParallelism)
    for (i in 1..globalBankParallelism) {
        operators.execute(BankOperator(queue, storeQueue, i, printer, starter, servicedClients, bankAccount, operatorsLeaved))
    }

    val sellers = Executors.newFixedThreadPool(globalSellerParallelism)
    for (i in 1..globalSellerParallelism) {
        sellers.execute(Seller(storeQueue, bankAccount))
    }

    val generator = ClientGenerator(queue, starter)

    Runtime.getRuntime().addShutdownHook(GracefulShutdownThread(operators, sellers, generator, bankAccount, servicedClients, operatorsLeaved))

    generator.start()
}