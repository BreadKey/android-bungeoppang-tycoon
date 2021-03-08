package com.example.fishcaketycoon.model

import io.reactivex.rxjava3.observers.TestObserver

import org.junit.Test

import org.junit.Assert.*

class FishcakeTest{
    @Test
    fun bakeTest() {
        val fishcake = Fishcake()

        val testSubscriber = TestObserver<Doneness>()

        fishcake.doneness.subscribe(testSubscriber)

        fishcake.bake(Fishcake.MEDIUM_SECONDS)
        fishcake.bake(Fishcake.WELL_DONE_SECONDS - Fishcake.MEDIUM_SECONDS)
        fishcake.bake(Fishcake.OVERCOOKED_SECONDS - Fishcake.WELL_DONE_SECONDS)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(Doneness.values().size)

        val values = testSubscriber.values()

        assertEquals(Doneness.Rare, values[0])
        assertEquals(Doneness.Medium, values[1])
        assertEquals(Doneness.WellDone, values[2])
        assertEquals(Doneness.Overcooked, values[3])
    }
}