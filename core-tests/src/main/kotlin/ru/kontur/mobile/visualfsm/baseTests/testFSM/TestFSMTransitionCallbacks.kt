package ru.kontur.mobile.visualfsm.baseTests.testFSM

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.transitioncallbacks.DefaultTransitionCallbacks

class TestFSMTransitionCallbacks<STATE : State, ACTION : Action<STATE>> : DefaultTransitionCallbacks<STATE, ACTION> {
    override fun onNoTransitionError(
        action: ACTION,
        currentState: STATE
    ) {
        throw IllegalStateException("onNoTransitionError $action $currentState")
    }

    override fun onMultipleTransitionError(
        action: ACTION,
        currentState: STATE,
        suitableTransitions: List<Transition<STATE, STATE>>
    ) {
        throw IllegalStateException("onMultipleTransitionError $action $currentState")
    }
}