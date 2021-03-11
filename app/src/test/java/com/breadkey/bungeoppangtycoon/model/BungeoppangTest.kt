package com.breadkey.bungeoppangtycoon.model

import io.reactivex.rxjava3.observers.TestObserver
import org.junit.After

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class BungeoppangTest{
    lateinit var bungeoppang: Bungeoppang
    lateinit var testSubscriber: TestObserver<Bungeoppang.State>

    @Before
    fun setUp() {
        bungeoppang = Bungeoppang()
        testSubscriber = TestObserver()
        bungeoppang.state.subscribe(testSubscriber)
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
        bungeoppang.flip()
        bakeUntilOvercooked()

        // First state is front and rare
        assertAllDonenessOccurred(after = 1) { state -> state.backDoneness }
    }

    private fun bakeUntilOvercooked() {
        repeat(Doneness.Overcooked.seconds.toInt()) {
            bungeoppang.bake(1.0)
        }
    }

    private fun assertAllDonenessOccurred(after: Int = 0, getDoneness: (state: Bungeoppang.State) -> Doneness) {
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
        bungeoppang.addCream(cream)

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
        bungeoppang.flip()
        assertCreamNotAdded(Cream.RedBean)
    }

    private fun assertCreamNotAdded(cream: Cream) {
        bungeoppang.addCream(cream)

        testSubscriber.assertNoErrors()

        val values = testSubscriber.values()
        assertNotEquals(cream, values.last().cream)
    }
}