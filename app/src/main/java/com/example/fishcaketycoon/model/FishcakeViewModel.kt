package com.example.fishcaketycoon.model

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

class FishcakeViewModel : ViewModel() {
    private val disposables = CompositeDisposable()
    val state = ObservableField<Fishcake.State>()

    fun setFishcake(fishcake: Fishcake) {
        disposables.clear()
        disposables.add(fishcake.state.subscribe {
            state.set(it)
        })
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}