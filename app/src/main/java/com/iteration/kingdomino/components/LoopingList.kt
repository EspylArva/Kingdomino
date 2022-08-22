package com.iteration.kingdomino.components;

import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import java.util.function.Consumer


class LoopingList<T> internal constructor(private var array: Array<Any?>) : MutableLiveData<ArrayList<T>>() {
    init {
        this.value = ArrayList()
    }

    constructor() : this(emptyArray()) {}
    constructor(elements: Collection<T>) : this(elements.toTypedArray<Any?>()) {
        this.value!!.addAll(elements)
    }

    fun cycle() {
        if(this.value!!.size > 2){
            this.value!!.add(this.value!!.first())
            this.value!!.removeAt(0)
            postValue(this.value)
        }
    }

    override fun toString(): String {
        return this.value.toString()
    }
}

fun <T> loopingListOf(vararg elements : T) : LoopingList<T> = if (elements.isEmpty()) LoopingList<T>(emptyArray()) else LoopingList<T>(elements.asList())
