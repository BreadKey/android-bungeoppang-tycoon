package com.example.fishcaketycoon.model

import java.util.*

class FishcakeTycoon {
    companion object {
        const val MOLD_COUNT = 9
    }

    private val fishcakes = Array<Fishcake?>(MOLD_COUNT) { null }
    private val cookedFishcakeQueue = LinkedList<Fishcake>()

    var seconds = 0.0
        private set

    fun start() {
        seconds = 0.0
        fishcakes.fill(null)
        cookedFishcakeQueue.clear()
    }

    fun getFishcakeAt(index: Int): Fishcake? {
        return fishcakes[index]
    }

    fun select(index: Int) {
        val fishcake = fishcakes[index]
        if (fishcake == null) {
            fishcakes[index] = Fishcake()
        } else {
            if (fishcake.currentState.isFront) {
                fishcake.flip()
            } else {
                catch(index)
            }
        }
    }

    private fun catch(index: Int) {
        cookedFishcakeQueue.add(fishcakes[index]!!)
        fishcakes[index] = null
    }

    internal fun update(delta: Double) {
        seconds += delta
        fishcakes.filterNotNull().forEach { fishcake ->
            fishcake.bake(delta)
        }
    }
}