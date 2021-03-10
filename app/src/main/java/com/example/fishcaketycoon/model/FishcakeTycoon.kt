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
        const val MOLD_LENGTH = 9
        const val START_MONEY = 1000
    }

    interface EventListener {
        fun onCookStart(at: Int, fishcake: Fishcake)
        fun onCookFinished(at: Int, fishcake: Fishcake)
    }

    private val fishcakes = Array<Fishcake?>(MOLD_LENGTH) { null }
    private val cookingFishcakes = mutableListOf<Fishcake>()
    private val cookedFishcakes = mutableListOf<Fishcake>()
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
        cookingFishcakes.clear()
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

    fun addCream(at: Int, cream: Cream) {
        fishcakes[at]?.addCream(cream)
    }

    private fun startCook(at: Int) {
        if (currentMoney >= Fishcake.DOUGH_COST) {
            val fishcake = Fishcake()
            fishcakes[at] = fishcake
            cookingFishcakes.add(fishcake)

            for (listener in listeners) {
                listener.onCookStart(at, fishcake)
            }

            moneySubject.onNext(currentMoney - Fishcake.DOUGH_COST)
        }
    }

    private fun finishCook(index: Int) {
        val fishcake = fishcakes[index]!!
        fishcakes[index] = null
        cookingFishcakes.remove(fishcake)
        cookedFishcakes.add(fishcake)

        for (listener in listeners) {
            listener.onCookFinished(index, fishcake)
        }
    }

    fun sale(fishcake: Fishcake) {
        cookedFishcakes.remove(fishcake)
        moneySubject.onNext(currentMoney + Fishcake.PRICE)
    }

    internal fun update(delta: Double) {
        seconds += delta
        cookingFishcakes.forEach { fishcake ->
            fishcake.bake(delta)
        }
    }
}