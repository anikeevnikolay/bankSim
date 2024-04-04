package org.example.thread

import org.example.model.BankAccount
import org.example.model.BankClient
import org.example.utils.log
import org.example.utils.stopSellers
import java.math.BigDecimal
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class Seller(
    private val storeQueue: BlockingQueue<BankClient>,
    private val bankAccount: BankAccount
) : Thread() {
    override fun run() {
        while (!stopSellers.get() || !storeQueue.isEmpty()) {
            val client = storeQueue.poll(1, TimeUnit.SECONDS) ?: continue
            log("Клиент $client посетил магазин")
            synchronized(bankAccount) {
                bankAccount.money += client.account.money
                client.account.money = BigDecimal.ZERO
            }
        }
        println("Продавец завершил работу")
    }
}