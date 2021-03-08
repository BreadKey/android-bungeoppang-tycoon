package com.example.fishcaketycoon.model

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

enum class Doneness {
    Rare, Medium, WellDone, Overcooked
}

class Fishcake {
    private val donenessSubject = BehaviorSubject.createDefault<Doneness>(Doneness.Rare)
    val doneness: Observable<Doneness> = donenessSubject

    private var bakedSeconds: Double = 0.0

    fun bake(seconds: Double) {
        bakedSeconds += seconds

        when {
            bakedSeconds >= OVERCOOKED_SECONDS -> donenessSubject.onNext(Doneness.Overcooked)
            bakedSeconds >= WELL_DONE_SECONDS -> donenessSubject.onNext(Doneness.WellDone)
            bakedSeconds >= MEDIUM_SECONDS -> donenessSubject.onNext(Doneness.Medium)
        }
    }

    companion object {
        const val MEDIUM_SECONDS = 5.0
        const val WELL_DONE_SECONDS = 9.0
        const val OVERCOOKED_SECONDS = 12.0
    }
}