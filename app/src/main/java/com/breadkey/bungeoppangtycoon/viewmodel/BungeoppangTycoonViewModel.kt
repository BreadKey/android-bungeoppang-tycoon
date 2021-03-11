package com.breadkey.bungeoppangtycoon.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.breadkey.bungeoppangtycoon.model.Cream
import com.breadkey.bungeoppangtycoon.model.Bungeoppang
import com.breadkey.bungeoppangtycoon.model.BungeoppangTycoon
import com.breadkey.bungeoppangtycoon.model.Customer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class BungeoppangTycoonViewModel @Inject constructor(private val bungeoppangTycoon: BungeoppangTycoon) :
    ViewModel(), BungeoppangTycoon.EventListener {
    private val bungeoppangSubscribers = Array<Disposable?>(BungeoppangTycoon.MOLD_LENGTH) {
        null
    }
    private val tycoonSubscribers = CompositeDisposable()

    val molds = List(BungeoppangTycoon.MOLD_LENGTH) {
        ObservableField<Bungeoppang.State?>()
    }

    val money = ObservableField<Int>()

    val customers = ObservableArrayList<Customer>()

    init {
        bungeoppangTycoon.addListener(this)
        tycoonSubscribers.addAll(bungeoppangTycoon.money.subscribe {
            money.set(it)
        })
        bungeoppangTycoon.start()
    }

    override fun onCleared() {
        bungeoppangTycoon.removeListener(this)
        bungeoppangTycoon.stop()
        bungeoppangSubscribers.forEach {
            it?.dispose()
        }
        tycoonSubscribers.clear()
        super.onCleared()
    }

    fun select(index: Int) {
        bungeoppangTycoon.select(index)
    }

    fun pause() {
        bungeoppangTycoon.pause()
    }

    fun resume() {
        bungeoppangTycoon.resume()
    }

    fun canAddCream(at: Int) = bungeoppangTycoon.getBungeoppangAt(at)?.canAddCream == true
    fun addCream(at: Int, cream: Cream) {
        bungeoppangTycoon.addCream(at, cream)
    }

    override fun onCookStart(at: Int, bungeoppang: Bungeoppang) {
        bungeoppangSubscribers[at]?.dispose()
        bungeoppangSubscribers[at] = bungeoppang.state.subscribe {
            molds[at].set(it)
        }
    }

    override fun onCookFinished(at: Int, bungeoppang: Bungeoppang) {
        bungeoppangSubscribers[at]?.dispose()
        molds[at].set(null)
    }

    override fun onCustomerCome(customer: Customer) {
        customers.add(customer)
    }

    override fun onCustomerOut(customer: Customer) {
        customers.remove(customer)
    }
}