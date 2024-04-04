package org.example.sharingobjects

import org.example.utils.globalSpeedKoef

class Printer {
    @Synchronized
    fun print() {
        Thread.sleep((0.3 * globalSpeedKoef).toLong())
    }
}