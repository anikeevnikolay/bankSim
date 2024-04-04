package org.example.model

import java.math.BigDecimal

data class BankClient(
    val id: Int,
    val priority: ClientPriority,
) {
    val startTime = System.currentTimeMillis()
    val account = BankAccount(BigDecimal.ZERO)

    override fun toString(): String {
        return "[$id, $priority]"
    }
}