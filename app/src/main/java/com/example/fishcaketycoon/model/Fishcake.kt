package com.example.fishcaketycoon.model

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

enum class Doneness {
    Rare, Medium, WellDone, Overcooked
}


class Fishcake {
    data class State(
        val frontDoneness: Doneness,
        val backDoneness: Doneness,
        val isFront: Boolean
    ) {
        fun copyWith(
            frontDoneness: Doneness = this.frontDoneness,
            backDoneness: Doneness = this.backDoneness,
            isFront: Boolean = this.isFront
        ) = State(
            frontDoneness, backDoneness, isFront
        )
    }

    private val stateSubject =
        BehaviorSubject.createDefault<State>(State(Doneness.Rare, Doneness.Rare, true))
    val state: Observable<State> = stateSubject.distinct()
    val currentState: State get() = stateSubject.value

    private var bakedSeconds: Double = 0.0

    internal fun bake(seconds: Double) {
        bakedSeconds += seconds

        val doneness = when {
            bakedSeconds >= OVERCOOKED_SECONDS -> Doneness.Overcooked
            bakedSeconds >= WELL_DONE_SECONDS -> Doneness.WellDone
            bakedSeconds >= MEDIUM_SECONDS -> Doneness.Medium
            else -> Doneness.Rare
        }

        if (currentState.isFront) {
            stateSubject.onNext(currentState.copyWith(frontDoneness = doneness))
        } else {
            stateSubject.onNext(currentState.copyWith(backDoneness = doneness))
        }
    }

    internal fun flip() {
        if (currentState.isFront) {
            bakedSeconds = 0.0
            stateSubject.onNext(currentState.copyWith(isFront = false))
        }
    }

    companion object {
        const val MEDIUM_SECONDS = 5.0
        const val WELL_DONE_SECONDS = 9.0
        const val OVERCOOKED_SECONDS = 12.0
    }
}