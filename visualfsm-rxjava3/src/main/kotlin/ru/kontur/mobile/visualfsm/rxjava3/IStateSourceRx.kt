package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Observable
import ru.kontur.mobile.visualfsm.IBaseStateSource
import ru.kontur.mobile.visualfsm.State

interface IStateSourceRx<STATE : State> : IBaseStateSource<STATE> {
    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    fun observeState(): Observable<STATE>
}