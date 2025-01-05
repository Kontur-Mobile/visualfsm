package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

interface LogFormatter<STATE : State, ACTION : Action<STATE>> {

    fun stateFormatter(state: STATE): String?

    fun actionFormatter(action: ACTION): String?
}
