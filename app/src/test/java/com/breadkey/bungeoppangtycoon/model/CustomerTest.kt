package com.breadkey.bungeoppangtycoon.model

import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Test

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

        repeat(Mood.values().size) {index ->
            testSubscriber.assertValueAt(index, Mood.values()[index])
        }

        testSubscriber.dispose()
    }
}