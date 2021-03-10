package com.example.fishcaketycoon.model

import io.reactivex.rxjava3.observers.TestObserver
import org.junit.After

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class FishcakeTest{
    lateinit var fishcake: Fishcake
    lateinit var testSubscriber: TestObserver<Fishcake.State>

    @Before
    fun setUp() {
        fishcake = Fishcake()
        testSubscriber = TestObserver()
        fishcake.state.subscribe(testSubscriber)
    }

    @After
    fun tearDown() {
        testSubscriber.dispose()
    }

    @Test
    fun bakeTest() {
        bakeUntilOvercooked()

        assertAllDonenessOccurred { state -> state.frontDoneness }
    }

    @Test
    fun bakeBackTest() {
        fishcake.flip()
        bakeUntilOvercooked()

        // First state is front and rare
        assertAllDonenessOccurred(after = 1) { state -> state.backDoneness }
    }

    private fun bakeUntilOvercooked() {
        repeat(Fishcake.OVERCOOKED_SECONDS.toInt()) {
            fishcake.bake(1.0)
        }
    }

    private fun assertAllDonenessOccurred(after: Int = 0, getDoneness: (state: Fishcake.State) -> Doneness) {
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(after + Doneness.values().size)

        val values = testSubscriber.values()

        assertEquals(Doneness.Rare, getDoneness(values[after]))
        assertEquals(Doneness.Medium, getDoneness(values[after + 1]))
        assertEquals(Doneness.WellDone, getDoneness(values[after + 2]))
        assertEquals(Doneness.Overcooked, getDoneness(values[after + 3]))
    }

    @Test
    fun addRedBeanCream() {
        assertCreamAdded(Cream.RedBean)
    }

    @Test
    fun addChouCream() {
        assertCreamAdded(Cream.Chou)
    }

    private fun assertCreamAdded(cream: Cream) {
        fishcake.addCream(cream)

        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(2)

        val values = testSubscriber.values()
        assertNull(values[0].cream)
        assertEquals(cream, values[1].cream)
    }

    @Test
    fun tryAddChouCreamAfterReadBean() {
        assertCreamAdded(Cream.RedBean)
        assertCreamNotAdded(Cream.Chou)
    }

    @Test
    fun tryAddRedBeanCreamAfterFilp() {
        fishcake.flip()
        assertCreamNotAdded(Cream.RedBean)
    }

    private fun assertCreamNotAdded(cream: Cream) {
        fishcake.addCream(cream)

        testSubscriber.assertNoErrors()

        val values = testSubscriber.values()
        assertNotEquals(cream, values.last().cream)
    }
}