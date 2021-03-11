package com.breadkey.bungeoppangtycoon.model

import org.junit.Test

import org.junit.Assert.*
class BungeoppangTycoonTest {
    @Test
    fun bakeMedium() {
        val tycoon = BungeoppangTycoon()
        tycoon.start()

        assertEquals(0.0, tycoon.seconds, 0.0)

        tycoon.update(2.0)

        assertEquals(2.0, tycoon.seconds, 2.0)

        assertNull(tycoon.getBungeoppangAt(0))
        tycoon.select(0)

        tycoon.update(Doneness.Medium.seconds)
        assertEquals(Doneness.Medium, tycoon.getBungeoppangAt(0)?.currentState?.frontDoneness)
    }
}