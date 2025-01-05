package ru.kontur.mobile.visualfsm

/**
 * Stores current [state][State] and proceeds an [actions][Action].
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param stateSource the [state source][IBaseStateSource] for storing and getting state,
 * can be external to implement a common state tree between parent and child state machines
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
 * on provided event calls (like logging, debugging, or metrics) (optional)
 */
abstract class BaseStore<STATE : State, ACTION : Action<STATE>>(
    private val stateSource: IBaseStateSource<STATE>,
    private val transitionCallbacks: TransitionCallbacks<STATE, ACTION>
) {

    init {
        transitionCallbacks.onInitialStateReceived(stateSource.getCurrentState())
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE {
        return stateSource.getCurrentState()
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    fun proceed(action: ACTION, transitionsFactory: TransitionsFactory<STATE, ACTION>) {
        action.setTransitions(transitionsFactory.create(action))
        val newState = reduce(action, getCurrentState())
        stateSource.updateState(newState)
    }

    /**
     * Runs [action's][Action] transition of [states][State]
     *
     * @param action launched [action][Action]
     * @param state new [state][State]
     * @return new [state][State]
     */
    private fun reduce(
        action: ACTION, state: STATE
    ): STATE {
        @Suppress("UNCHECKED_CAST")
        return action.run(state, transitionCallbacks as TransitionCallbacks<STATE, Action<STATE>>)
    }
}