package com.breadkey.bungeoppangtycoon.model

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BungeoppangTycoon @Inject constructor() {
    companion object {
        const val MOLD_LENGTH = 9
        const val START_MONEY = 1000
    }

    interface EventListener {
        fun onCookStart(at: Int, bungeoppang: Bungeoppang)
        fun onCookFinished(at: Int, bungeoppang: Bungeoppang)
    }

    private val bungeoppangs = Array<Bungeoppang?>(MOLD_LENGTH) { null }
    private val cookingBungeoppangs = mutableListOf<Bungeoppang>()
    private val cookedBungeoppangs = mutableListOf<Bungeoppang>()
    private val listeners = mutableListOf<EventListener>()
    var seconds = 0.0
        private set
    private var timer: Disposable? = null

    private val moneySubject = BehaviorSubject.create<Int>()
    val money: Observable<Int> get() = moneySubject.distinct()
    val currentMoney: Int get() = moneySubject.value

    fun addListener(listener: EventListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: EventListener) {
        listeners.remove(listener)
    }

    fun start() {
        seconds = 0.0
        bungeoppangs.fill(null)
        cookedBungeoppangs.clear()
        cookingBungeoppangs.clear()
        moneySubject.onNext(START_MONEY)

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

    fun select(index: Int) {
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

    fun sale(bungeoppang: Bungeoppang) {
        cookedBungeoppangs.remove(bungeoppang)
        moneySubject.onNext(currentMoney + Bungeoppang.PRICE)
    }

    internal fun update(delta: Double) {
        seconds += delta
        cookingBungeoppangs.forEach { bungeoppang ->
            bungeoppang.bake(delta)
        }
    }
}