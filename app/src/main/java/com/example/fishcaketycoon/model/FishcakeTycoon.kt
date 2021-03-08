package com.example.fishcaketycoon.model

class FishcakeTycoon {
    companion object {
        const val MOLD_COUNT = 9
    }

    private val fishcakes = Array<Fishcake?>(MOLD_COUNT) { null }
    var seconds = 0.0
        private set

    fun start() {
        seconds = 0.0
        fishcakes.fill(null)
    }

    fun getFishcakeAt(index: Int): Fishcake? {
        return fishcakes[index]
    }

    fun select(index: Int) {
        if (fishcakes[index] == null) {
            fishcakes[index] = Fishcake()
        }
    }

    internal fun update(delta: Double) {
        seconds += delta
        fishcakes.filterNotNull().forEach { fishcake ->
            fishcake.bake(delta)
        }
    }
}