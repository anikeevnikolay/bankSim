package org.example.thread

import org.example.utils.globalSpeedKoef
import org.example.utils.log
import org.example.model.BankClient
import org.example.model.ClientPriority
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.PriorityBlockingQueue

class ClientGenerator(
    private val queue: PriorityBlockingQueue<BankClient>,
    private val starter: CountDownLatch,
) : Thread() {
    override fun run() {
        try {
            var i = 0
            val random = Random()
            while (!isInterrupted) {
                queue.add(BankClient(++i, ClientPriority.fromOrder(random.nextInt(3))).apply {
                    log("Клиент $this пришел в банк")
                })
                sleep((0.5 * globalSpeedKoef).toLong())
                starter.countDown()
            }
        } catch (e: InterruptedException) {
            log("Поток клиентов остановился")
            this.interrupt()
        }
    }
}