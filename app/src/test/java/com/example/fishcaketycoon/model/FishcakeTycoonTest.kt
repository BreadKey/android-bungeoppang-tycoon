package com.example.fishcaketycoon.model

import org.junit.Test

import org.junit.Assert.*
class FishcakeTycoonTest {
    @Test
    fun bakeMedium() {
        val tycoon = FishcakeTycoon()
        tycoon.start()

        assertEquals(0.0, tycoon.seconds, 0.0)

        tycoon.update(2.0)

        assertEquals(2.0, tycoon.seconds, 2.0)

        assertNull(tycoon.getFishcakeAt(0))
        tycoon.select(0)

        tycoon.update(Fishcake.MEDIUM_SECONDS)
        assertEquals(Doneness.Medium, tycoon.getFishcakeAt(0)?.currentState?.frontDoneness)
    }
}