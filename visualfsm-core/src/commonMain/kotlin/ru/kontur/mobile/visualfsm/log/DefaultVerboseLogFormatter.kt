package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

class DefaultVerboseLogFormatter<STATE : State, ACTION : Action<STATE>>
    : LogFormatter<STATE, ACTION> {

    override fun stateFormatter(state: STATE): String {
        return state.toString()
    }

    override fun actionFormatter(action: ACTION): String {
        return action.toString()
    }
}