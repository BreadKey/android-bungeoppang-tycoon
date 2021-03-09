package com.example.fishcaketycoon.model

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FishcakeTycoon @Inject constructor() {
    companion object {
        const val MOLD_COUNT = 9
        const val START_MONEY = 1000
    }

    interface EventListener {
        fun onCookStart(at: Int, fishcake: Fishcake)
        fun onCookFinished(at: Int, fishcake: Fishcake)
    }

    private val fishcakes = Array<Fishcake?>(MOLD_COUNT) { null }
    private val cookedFishcakes = mutableSetOf<Fishcake>()
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
        fishcakes.fill(null)
        cookedFishcakes.clear()
        moneySubject.onNext(START_MONEY)

        timer?.dispose()
        timer = Observable.interval(1, TimeUnit.SECONDS).subscribe {
            update(1.0)
        }
    }

    fun stop() {
        timer?.dispose()
    }

    fun getFishcakeAt(index: Int): Fishcake? {
        return fishcakes[index]
    }

    fun select(index: Int) {
        val fishcake = fishcakes[index]
        if (fishcake == null) {
            startCook(index)
        } else {
            if (fishcake.currentState.isFront) {
                fishcake.flip()
            } else {
                finishCook(index)
            }
        }
    }

    private fun startCook(at: Int) {
        if (currentMoney >= Fishcake.DOUGH_COST) {
            val fishcake = Fishcake()
            fishcakes[at] = fishcake
            listeners.forEach {
                it.onCookStart(at, fishcake)
            }

            moneySubject.onNext(currentMoney - Fishcake.DOUGH_COST)
        }
    }

    private fun finishCook(index: Int) {
        val fishcake = fishcakes[index]!!
        cookedFishcakes.add(fishcake)
        fishcakes[index] = null
        for (listener in listeners) {
            listener.onCookFinished(index, fishcake)
        }
    }

    fun sale(fishcake: Fishcake) {
        moneySubject.onNext(currentMoney + Fishcake.PRICE)
    }

    internal fun update(delta: Double) {
        seconds += delta
        fishcakes.filterNotNull().forEach { fishcake ->
            fishcake.bake(delta)
        }
    }
}