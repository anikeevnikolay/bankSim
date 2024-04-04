package org.example.thread

import org.example.model.BankAccount
import org.example.model.BankClient
import org.example.model.ClientPriority
import org.example.sharingobjects.DocumentChecker
import org.example.sharingobjects.Printer
import org.example.utils.globalSpeedKoef
import org.example.utils.log
import org.example.utils.stopOperators
import java.math.BigDecimal
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class BankOperator(
    private val queue: PriorityBlockingQueue<BankClient>,
    private val storeQueue: BlockingQueue<BankClient>,
    private val number: Number,
    private val printer: Printer,
    private val starter: CountDownLatch,
    private val servicedClients: MutableMap<ClientPriority, AtomicLong>,
    private val bankAccount: BankAccount,
    private val operatorsLeaved: CountDownLatch
) : Thread() {

    override fun run() {
        starter.await()
        while (!stopOperators.get() || !queue.isEmpty()) {
            val client = queue.poll(1, TimeUnit.SECONDS) ?: continue
            log("Оператор $this начал обслуживать клиента $client")

            val documentChecker = DocumentChecker.getInstance()
            if (client.priority.needDocUpdate) {
                documentChecker.writeDocs(client, this)
            } else {
                documentChecker.checkDocs(client, this)
            }

            sleep((client.priority.serviceTime * globalSpeedKoef).toLong())

            printClient(client)

            val time = System.currentTimeMillis() - client.startTime
            log("Оператор $this закончил обслуживать клиента $client за $time ms")

            if (client.priority == ClientPriority.CREDIT) {
                synchronized(bankAccount) {
                    client.account.money = BigDecimal.valueOf(100)
                    bankAccount.money -= BigDecimal.valueOf(100)
                    storeQueue.add(client)
                }
            }

            servicedClients.computeIfAbsent(client.priority) { AtomicLong(0) }.incrementAndGet()
        }
        log("Оператор $this ушел с работы")
        operatorsLeaved.countDown()
    }

    private fun printClient(client: BankClient) {
        log("Оператор $this начал печатать документы клиента $client")
        printer.print()
        log("Оператор $this закончил печатать документы клиента $client")
    }

    override fun toString(): String = number.toString()
}