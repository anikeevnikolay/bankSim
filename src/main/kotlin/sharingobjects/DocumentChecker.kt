package org.example.sharingobjects

import org.example.utils.globalSpeedKoef
import org.example.utils.log
import org.example.model.BankClient
import org.example.thread.BankOperator
import java.util.concurrent.locks.ReentrantReadWriteLock

class DocumentChecker {
    private val readWriteLock = ReentrantReadWriteLock()
    private val readLock = readWriteLock.readLock()
    private val writeLock = readWriteLock.writeLock()

    companion object {
        private var instance: DocumentChecker? = null

        fun getInstance(): DocumentChecker {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = DocumentChecker()
                    }
                }
            }
            return instance!!
        }
    }

    fun checkDocs(client: BankClient, operator: BankOperator) {
        try {
            log("Оператор $operator начал проверку документов клиента $client")
            readLock.lock()
            Thread.sleep((0.3 * globalSpeedKoef).toLong())
        } finally {
            readLock.unlock()
        }
        log("Оператор $operator закончил проверку документов клиента $client")
    }

    fun writeDocs(client: BankClient, operator: BankOperator) {
        try {
            log("Оператор $operator начал обновление документов клиента $client")
            writeLock.lock()
            Thread.sleep((0.3 * globalSpeedKoef).toLong())
        } finally {
            writeLock.unlock()
        }
        log("Оператор $operator закончил обновление документов клиента $client")
    }
}