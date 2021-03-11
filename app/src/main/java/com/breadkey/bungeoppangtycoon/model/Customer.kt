package com.breadkey.bungeoppangtycoon.model

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

enum class Mood(val satisfaction: Int) {
    VeryHappy(95),
    Happy(85),
    Satisfied(70),
    Neutral(50),
    Disappointed(35),
    Grumpy(20),
    VeryGrumpy(10),
    Furious(0)
}

class Customer(
    initialSatisfaction: Double
) {
    data class Order(val redBean: Int, val chou: Int)

    private var satisfaction: Double = initialSatisfaction
    private val moodSubject =
        BehaviorSubject.createDefault<Mood>(moodFromSatisfaction(initialSatisfaction))
    val mood: Observable<Mood> = moodSubject.distinct()
    val currentMood: Mood get() = moodSubject.value

    private fun moodFromSatisfaction(satisfaction: Double): Mood {
        for (mood in Mood.values()) {
            if (mood.satisfaction < satisfaction)
                return mood
        }

        return Mood.Furious
    }

    internal fun update(delta: Double) {
        satisfaction -= delta

        if (currentMood != Mood.Furious) {
            val nextMood = Mood.values()[currentMood.ordinal + 1]

            if (satisfaction <= nextMood.satisfaction) {
                moodSubject.onNext(nextMood)
            }
        }
    }
}