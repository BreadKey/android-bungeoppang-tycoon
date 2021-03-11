package com.breadkey.bungeoppangtycoon.model

import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class BungeoppangTycoonTest {
    private lateinit var tycoon: BungeoppangTycoon

    @Before
    fun setUp() {
        tycoon = BungeoppangTycoon()
        tycoon.start()
    }

    @After
    fun tearDown() {
        tycoon.stop()
    }

    @Test
    fun bakeMedium() {
        tycoon.start()

        assertEquals(0.0, tycoon.seconds, 0.0)

        tycoon.update(2.0)

        assertEquals(2.0, tycoon.seconds, 2.0)

        assertNull(tycoon.getBungeoppangAt(0))
        tycoon.select(0)

        tycoon.update(Doneness.Medium.seconds)
        assertEquals(Doneness.Medium, tycoon.getBungeoppangAt(0)?.currentState?.frontDoneness)
    }

    @Test
    fun saleSucceed() {
        val customer = Customer(100.0, Customer.Order(1, 0))

        tycoon.sale(listOf(generatePerfectBungeoppang(Cream.RedBean)), customer)

        assertEquals(BungeoppangTycoon.START_MONEY + Bungeoppang.PRICE, tycoon.currentMoney)
    }

    @Test
    fun saleFailed() {
        val customer = Customer(100.0, Customer.Order(1, 0))

        tycoon.sale(listOf(generatePerfectBungeoppang(Cream.Chou)), customer)

        assertEquals(BungeoppangTycoon.START_MONEY, tycoon.currentMoney)
    }
}