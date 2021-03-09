package com.example.fishcaketycoon.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.fishcaketycoon.model.Fishcake
import com.example.fishcaketycoon.model.FishcakeTycoon
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class FishcakeTycoonViewModel @Inject constructor(private val fishcakeTycoon: FishcakeTycoon) :
    ViewModel(), FishcakeTycoon.EventListener {
    private val fishcakesSubscribers = Array<Disposable?>(FishcakeTycoon.MOLD_COUNT) {
        null
    }

    val molds = List(FishcakeTycoon.MOLD_COUNT) {
        ObservableField<Fishcake.State?>()
    }

    init {
        fishcakeTycoon.addListener(this)
        fishcakeTycoon.start()
    }

    override fun onCleared() {
        fishcakeTycoon.removeListener(this)
        fishcakeTycoon.stop()
        fishcakesSubscribers.forEach {
            it?.dispose()
        }
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
    }

    fun select(index: Int) {
        fishcakeTycoon.select(index)
    }
}