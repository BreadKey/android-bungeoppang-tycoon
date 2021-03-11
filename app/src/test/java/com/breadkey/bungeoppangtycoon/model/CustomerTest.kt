package com.breadkey.bungeoppangtycoon.model

import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Test
import org.junit.Assert.*

class CustomerTest {
    @Test
    fun changeMood() {
        val customer = Customer(100.0)
        val testSubscriber = TestObserver<Mood>()

        customer.mood.subscribe(testSubscriber)

        repeat(100) {
            customer.update(1.0)
        }

        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(Mood.values().size)

        repeat(Mood.values().size) { index ->
            testSubscriber.assertValueAt(index, Mood.values()[index])
        }

        customer.satisfaction = 100.0
        customer.payFor(emptyList())

        assertEquals(Mood.VeryHappy, testSubscriber.values().last())

        testSubscriber.dispose()
    }

    @Test
    fun payForPerfectBungeoppangs() {
        val customer = Customer(100.0, Customer.Order(2, 2))
        val bungeoppangs = listOf(
            generatePerfectBungeoppang(Cream.RedBean),
            generatePerfectBungeoppang(Cream.RedBean),
            generatePerfectBungeoppang(Cream.Chou),
            generatePerfectBungeoppang(Cream.Chou)
        )

        assertEquals(Bungeoppang.PRICE * 4, customer.payFor(bungeoppangs))
    }

    @Test
    fun payForUnorderedBungeoppang() {
        val customer = Customer(100.0, Customer.Order(1, 0))

        assertEquals(0, customer.payFor(listOf(generatePerfectBungeoppang(Cream.Chou))))
        assertEquals(Mood.Furious, customer.currentMood)
    }

    @Test
    fun calculatePerfectBungeoppangScore() {
        val customer = Customer(100.0)

        assertEquals(
            100,
            customer.calculateBungeoppangScore(generatePerfectBungeoppang(Cream.RedBean))
        )
    }
}