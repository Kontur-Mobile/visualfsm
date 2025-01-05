package ru.kontur.mobile.visualfsm

import kotlinx.coroutines.flow.StateFlow

interface IStateSource<STATE : State> : IBaseStateSource<STATE> {

    /**
     * Provides a [flow][StateFlow] of [states][State]
     *
     * @return a [flow][StateFlow] of [states][State]
     */
    fun observeState(): StateFlow<STATE>
}