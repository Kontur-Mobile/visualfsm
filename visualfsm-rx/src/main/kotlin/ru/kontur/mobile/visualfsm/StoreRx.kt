package ru.kontur.mobile.visualfsm

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

/**
 * Stores current [state][State] and provides subscription on [state][State] updates.
 * It is the core of the state machine, takes an [action][Action] as input and returns [states][State] as output
 *
 * @param initialState initial [state][State]
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] that used on some [Action] and [Transition] actions
 */
abstract class StoreRx<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE,
    private val transitionCallbacks: TransitionCallbacks<STATE>
) {

    private var currentState = initialState
    private val stateRxObservableField = BehaviorSubject.createDefault(initialState).toSerialized()

    /**
     * Provides a [flow][Flow] of [states][State]
     *
     * @return a [flow][Flow] of [states][State]
     */
    internal fun observeState(): Observable<STATE> {
        return stateRxObservableField
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    @Synchronized
    internal fun getStateSingle(): Single<STATE> {
        return Single.fromCallable { currentState }
    }

    /**
     * Changes current state
     *
     * @param action [Action] that was launched
     */
    @Synchronized
    internal fun proceed(action: ACTION) {
        val newState = reduce(action, currentState)
        val changed = newState != currentState
        if (changed) {
            currentState = newState
            stateRxObservableField.onNext(newState)
        }
    }

    /**
     * Runs [action's][Action] transition of [states][State]
     *
     * @param action launched [action][Action]
     * @param state new [state][State]
     * @return new [state][State]
     */
    private fun reduce(
        action: ACTION,
        state: STATE
    ): STATE {
        return action.run(state, transitionCallbacks)
    }
}