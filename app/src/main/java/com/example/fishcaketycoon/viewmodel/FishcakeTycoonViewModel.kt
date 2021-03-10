package com.example.fishcaketycoon.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.fishcaketycoon.model.Cream
import com.example.fishcaketycoon.model.Fishcake
import com.example.fishcaketycoon.model.FishcakeTycoon
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class FishcakeTycoonViewModel @Inject constructor(private val fishcakeTycoon: FishcakeTycoon) :
    ViewModel(), FishcakeTycoon.EventListener {
    private val fishcakesSubscribers = Array<Disposable?>(FishcakeTycoon.MOLD_LENGTH) {
        null
    }
    private val tycoonSubscribers = CompositeDisposable()

    val molds = List(FishcakeTycoon.MOLD_LENGTH) {
        ObservableField<Fishcake.State?>()
    }

    val money = ObservableField<Int>()

    init {
        fishcakeTycoon.addListener(this)
        tycoonSubscribers.addAll(fishcakeTycoon.money.subscribe {
            money.set(it)
        })
        fishcakeTycoon.start()
    }

    override fun onCleared() {
        fishcakeTycoon.removeListener(this)
        fishcakeTycoon.stop()
        fishcakesSubscribers.forEach {
            it?.dispose()
        }
        tycoonSubscribers.clear()
        super.onCleared()
    }

    override fun onCookStart(at: Int, fishcake: Fishcake) {
        fishcakesSubscribers[at]?.dispose()
        fishcakesSubscribers[at] = fishcake.state.subscribe {
            molds[at].set(it)
        }
    }

    override fun onCookFinished(at: Int, fishcake: Fishcake) {
        fishcakesSubscribers[at]?.dispose()
        molds[at].set(null)

        // Just Test
        fishcakeTycoon.sale(fishcake)
    }

    fun select(index: Int) {
        fishcakeTycoon.select(index)
    }

    fun pause() {
        fishcakeTycoon.pause()
    }

    fun resume() {
        fishcakeTycoon.resume()
    }

    fun canAddCream(at: Int) = fishcakeTycoon.getFishcakeAt(at)?.canAddCream == true
    fun addCream(at: Int, cream: Cream) {
        fishcakeTycoon.addCream(at, cream)
    }
}