package com.breadkey.bungeoppangtycoon.model

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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
    initialSatisfaction: Double,
    val order: Order = Order(0, 0)
) {
    data class Order(val redBean: Int, val chou: Int) {
        val total = redBean + chou
    }
    data class Evaluation(val correctCount: Int, val score: Int)

    private var satisfaction: Double = initialSatisfaction
    private val moodSubject =
        BehaviorSubject.createDefault<Mood>(moodFromSatisfaction())
    val mood: Observable<Mood> = moodSubject.distinct()
    val currentMood: Mood get() = moodSubject.value

    private fun moodFromSatisfaction(): Mood {
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

    fun payFor(bungeoppangs: List<Bungeoppang>): Int {
        val evaluation = evaluate(bungeoppangs)

        val amount =
            (evaluation.correctCount * Bungeoppang.PRICE * (evaluation.score / 100.0)).roundToInt()

        satisfaction += evaluation.score / if (evaluation.score > 0) 2 else 1

        moodSubject.onNext(moodFromSatisfaction())

        return max(0, amount)
    }

    private fun evaluate(bungeoppangs: List<Bungeoppang>): Evaluation {
        if (order.total == 0) return Evaluation(0, 0)

        var redBeanCount = 0
        var chouCount = 0

        var bungeoppangScore = 0

        for (bungeoppang in bungeoppangs) {
            bungeoppang.currentState.cream?.let { cream ->
                if (cream == Cream.RedBean) {
                    redBeanCount++
                    if (redBeanCount <= order.redBean) {
                        bungeoppangScore += calculateBungeoppangScore(bungeoppang)
                    }
                } else if (cream == Cream.Chou) {
                    chouCount++
                    if (chouCount <= order.chou) {
                        bungeoppangScore += calculateBungeoppangScore(bungeoppang)
                    }
                }
            }
        }

        var score = bungeoppangScore / order.total

        val correctCount = min(order.redBean, redBeanCount) + min(order.chou, chouCount)
        val correctCountDecline = 100 * (order.total - correctCount) / order.total

        score -= correctCountDecline

        return Evaluation(correctCount, score)
    }

    fun calculateBungeoppangScore(bungeoppang: Bungeoppang): Int {
        var score = 0
        score += calculateDonenessScore(bungeoppang.currentState.frontDoneness)
        score += calculateDonenessScore(bungeoppang.currentState.backDoneness)

        return score / 2
    }

    fun calculateDonenessScore(doneness: Doneness): Int = when(doneness) {
        Doneness.WellDone -> 100
        Doneness.Overcooked -> -70
        Doneness.Rare -> -50
        Doneness.Medium -> 0
    }
}