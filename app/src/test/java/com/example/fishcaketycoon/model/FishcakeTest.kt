package com.example.fishcaketycoon.model

import io.reactivex.rxjava3.observers.TestObserver

import org.junit.Test

import org.junit.Assert.*

class FishcakeTest{
    @Test
    fun bakeTest() {
        val fishcake = Fishcake()

        val testSubscriber = TestObserver<Fishcake.State>()

        fishcake.state.subscribe(testSubscriber)

        repeat(Fishcake.OVERCOOKED_SECONDS.toInt()) {
            fishcake.bake(it.toDouble())
        }

        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(Doneness.values().size)

        val values = testSubscriber.values()

        assertEquals(Doneness.Rare, values[0].frontDoneness)
        assertEquals(Doneness.Medium, values[1].frontDoneness)
        assertEquals(Doneness.WellDone, values[2].frontDoneness)
        assertEquals(Doneness.Overcooked, values[3].frontDoneness)
    }

    @Test
    fun bakeBackTest() {
        val fishcake = Fishcake()
    }
}