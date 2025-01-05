package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

class DefaultInfoLogFormatter<STATE : State, ACTION : Action<STATE>>
    : LogFormatter<STATE, ACTION> {

    override fun stateFormatter(state: STATE): String {
        return state::class.simpleName ?: "State"
    }

    override fun actionFormatter(action: ACTION): String {
        return action::class.simpleName ?: "Action"
    }
}