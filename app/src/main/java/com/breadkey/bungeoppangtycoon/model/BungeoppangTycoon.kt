package com.breadkey.bungeoppangtycoon.model

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.random.Random

@Singleton
class BungeoppangTycoon @Inject constructor() {
    companion object {
        const val MOLD_LENGTH = 9
        const val MAX_CUSTOMER_LENGTH = 3
        const val START_MONEY = 1000
        const val MIN_CUSTOMER_FOR_GRADE = 5
        private const val MIN_CUSTOMER_APPEARANCE_SECONDS = 3.0
    }

    interface EventListener {
        fun onCookStart(at: Int, bungeoppang: Bungeoppang)
        fun onCookFinished(at: Int, bungeoppang: Bungeoppang)
        fun onCustomerCome(customer: Customer)
        fun onCustomerOut(customer: Customer)
        fun onGameOver()
    }

    private val bungeoppangs = Array<Bungeoppang?>(MOLD_LENGTH) { null }
    private val cookingBungeoppangs = mutableListOf<Bungeoppang>()
    private val cookedBungeoppangs = mutableListOf<Bungeoppang>()
    private val customers = mutableListOf<Customer>()
    private var customerCount = 0
    private var totalScore = 0.0
    private val listeners = mutableListOf<EventListener>()

    var seconds = 0.0
        private set
    private var timer: Disposable? = null
    private val canBringCustomer: Boolean
        get() = seconds % MIN_CUSTOMER_APPEARANCE_SECONDS == 0.0 &&
                rollDice(3) == 0

    private val moneySubject = BehaviorSubject.create<Int>()
    val money: Observable<Int> get() = moneySubject.distinctUntilChanged()
    val currentMoney: Int get() = moneySubject.value

    private val gradeSubject = BehaviorSubject.create<Int>()
    val grade: Observable<Int> get() = gradeSubject.distinctUntilChanged()
    val currentGrade: Int get() = gradeSubject.value

    private val isBankruptcy get():Boolean = currentMoney == 0 && cookingBungeoppangs.isEmpty() && cookedBungeoppangs.isEmpty()

    private fun rollDice(range: Int) = Random.nextInt(range)

    fun addListener(listener: EventListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: EventListener) {
        listeners.remove(listener)
    }

    fun start() {
        seconds = 0.0
        bungeoppangs.fill(null)
        cookingBungeoppangs.clear()
        cookedBungeoppangs.clear()
        customers.clear()
        moneySubject.onNext(START_MONEY)
        totalScore = 0.0
        customerCount = 0
        gradeSubject.onNext(0)

        resume()
    }

    fun resume() {
        timer?.dispose()
        timer = Observable.interval(1, TimeUnit.SECONDS).subscribe {
            update(1.0)
        }
    }

    fun stop() {
        pause()
    }

    fun pause() {
        timer?.dispose()
    }

    fun getBungeoppangAt(index: Int): Bungeoppang? {
        return bungeoppangs[index]
    }

    fun cook(index: Int) {
        val bungeoppang = bungeoppangs[index]
        if (bungeoppang == null) {
            startCook(index)
        } else {
            if (bungeoppang.currentState.isFront) {
                bungeoppang.flip()
            } else {
                finishCook(index)
            }
        }
    }

    fun addCream(at: Int, cream: Cream) {
        bungeoppangs[at]?.addCream(cream)
    }

    private fun startCook(at: Int) {
        if (currentMoney >= Bungeoppang.DOUGH_COST) {
            val bungeoppang = Bungeoppang()
            bungeoppangs[at] = bungeoppang
            cookingBungeoppangs.add(bungeoppang)

            for (listener in listeners) {
                listener.onCookStart(at, bungeoppang)
            }

            moneySubject.onNext(currentMoney - Bungeoppang.DOUGH_COST)
        }
    }

    private fun finishCook(index: Int) {
        val bungeoppang = bungeoppangs[index]!!
        bungeoppangs[index] = null
        cookingBungeoppangs.remove(bungeoppang)
        cookedBungeoppangs.add(bungeoppang)

        for (listener in listeners) {
            listener.onCookFinished(index, bungeoppang)
        }
    }

    fun sale(bungeoppangs: List<Bungeoppang>, to: Customer): Boolean {
        if (!customers.contains(to)) return false

        val amount = to.payFor(bungeoppangs)
        moneySubject.onNext(currentMoney + amount)

        customers.remove(to)
        cookedBungeoppangs.removeAll(bungeoppangs)

        Observable.timer(1, TimeUnit.SECONDS).subscribe {
            customerOut(to)
            if (isBankruptcy) {
                gameOver()
            }
        }

        return true
    }


    private fun customerOut(customer: Customer) {
        for (listener in listeners) {
            listener.onCustomerOut(customer)
        }

        customerCount++
        totalScore += customer.satisfaction

        val grade = ((totalScore / customerCount) / 10).roundToInt()

        if (customerCount >= MIN_CUSTOMER_FOR_GRADE) {
            gradeSubject.onNext(grade)

            if (grade <= 1) {
                gameOver()
            }
        }
    }

    private fun gameOver() {
        stop()
        for (listener in listeners) {
            listener.onGameOver()
        }
    }

    internal fun update(delta: Double) {
        seconds += delta
        bakeBungeoppangs(delta)
        updateCustomers(delta)
        bringCustomerByRandom()
    }

    private fun bakeBungeoppangs(delta: Double) {
        cookingBungeoppangs.forEach { bungeoppang ->
            bungeoppang.bake(delta)
        }
    }

    private fun updateCustomers(delta: Double) {
        customers.toList().forEach { customer ->
            customer.update(delta)

            if (customer.satisfaction <= 0.0) {
                customers.remove(customer)
                customerOut(customer)
            }
        }
    }

    private fun bringCustomerByRandom() {
        if (customers.size < MAX_CUSTOMER_LENGTH) {
            if (canBringCustomer) {
                val bungeoppangCount = rollDice(6) + 1
                val redBeanCount = rollDice(bungeoppangCount)

                val initialSatisfaction = 100.0 - rollDice(5) * 10
                bringCustomer(
                    initialSatisfaction,
                    Customer.Order(redBeanCount, bungeoppangCount - redBeanCount)
                )
            }
        }
    }

    internal fun bringCustomer(initialSatisfaction: Double, order: Customer.Order) {
        val customer =
            Customer(
                initialSatisfaction,
                order
            )

        customers.add(
            customer
        )

        for (listener in listeners) {
            listener.onCustomerCome(customer)
        }
    }
}