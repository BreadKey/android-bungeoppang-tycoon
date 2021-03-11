package com.breadkey.bungeoppangtycoon.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.breadkey.bungeoppangtycoon.model.*
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

    private val customerSubscribers = mutableListOf<Disposable>()

    val customers = ObservableArrayList<Customer>()

    private val selectedBungeoppangs = mutableListOf<Bungeoppang>()

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
        customerSubscribers.forEach {
            it.dispose()
        }
        tycoonSubscribers.clear()
        super.onCleared()
    }

    fun cook(index: Int) {
        bungeoppangTycoon.cook(index)
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

    fun sale(customer: Customer?) {
        if (customer != null)
            bungeoppangTycoon.sale(
                listOf(
                    Bungeoppang(
                        Bungeoppang.State(
                            Doneness.WellDone,
                            Doneness.WellDone,
                            false,
                            Cream.RedBean
                        )
                    ),
                    Bungeoppang(
                        Bungeoppang.State(
                            Doneness.WellDone,
                            Doneness.WellDone,
                            false,
                            Cream.RedBean
                        )
                    ),
                    Bungeoppang(
                        Bungeoppang.State(
                            Doneness.WellDone,
                            Doneness.WellDone,
                            false,
                            Cream.Chou
                        )
                    ),
                    Bungeoppang(
                        Bungeoppang.State(
                            Doneness.WellDone,
                            Doneness.WellDone,
                            false,
                            Cream.Chou
                        )
                    )
                ), customer
            )
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
        customerSubscribers.add(customer.mood.subscribe {
            customers[customers.indexOf(customer)] = customer
            customers
        })
    }

    override fun onCustomerOut(customer: Customer) {
        val index = customers.indexOf(customer)
        customerSubscribers[index].dispose()
        customerSubscribers.removeAt(index)
        customers.removeAt(index)
    }
}