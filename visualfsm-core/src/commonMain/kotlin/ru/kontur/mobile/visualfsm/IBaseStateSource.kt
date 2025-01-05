package ru.kontur.mobile.visualfsm

/**
 * Base state source used for synchronous access to state
 */
interface IBaseStateSource<STATE : State> {

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE

    /**
     * Update current state
     *
     * @param newState [State] for update
     */
    fun updateState(newState: STATE)
}