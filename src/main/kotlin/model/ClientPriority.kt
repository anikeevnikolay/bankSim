package org.example.model

enum class ClientPriority(val priorValue: Int, val serviceTime: Double, val needDocUpdate: Boolean) {
    CREDIT(1, 1.0, true),
    DEBIT(2, 2.0, false),
    INSURANCE(2, 1.5, false);

    companion object {
        private val mapByOrder = entries.associateBy { it.ordinal }

        fun fromOrder(i: Int) = mapByOrder[i]!!
    }
}