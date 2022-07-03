package ru.kontur.mobile.visualfsm.rxjava2

import io.reactivex.Observable
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.TransitionFactory

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 */
open class FeatureRx<STATE : State, ACTION : Action<STATE>>
@Deprecated(
    message = "Deprecated, because it not support code generation.\n" +
            "Code generation not configured or configured incorrectly.\n" +
            "See the readme file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/eng/Quickstart-ENG.md).",
    replaceWith = ReplaceWith("Constructor with transitionFactory parameter.")
)
constructor(
    initialState: STATE,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
    transitionCallbacks: TransitionCallbacks<STATE>? = null,
) {

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionFactory a [TransitionFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionFactory: TransitionFactory<STATE, ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.transitionFactory = transitionFactory
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionFactory a function that returns a [TransitionFactory] instance to create the transition list for the action
     */
    @Suppress("DEPRECATION")
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionFactory: FeatureRx<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION>,
    ) : this(initialState, asyncWorker, transitionCallbacks) {
        this.transitionFactory = transitionFactory(this)
    }

    private var getTransitionFactory: (FeatureRx<STATE, ACTION>.() -> TransitionFactory<STATE, ACTION>)? = null

    private var transitionFactory: TransitionFactory<STATE, ACTION>? = null

    private val store = StoreRx<STATE, ACTION>(initialState, transitionCallbacks)

    init {
        asyncWorker?.bind(this)
    }

    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    fun observeState(): Observable<STATE> {
        return store.observeState()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    fun getCurrentState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][StoreRx]
     *
     * @param action [Action] to run
     */
    fun proceed(action: ACTION) {
        val transitionFactory = this.transitionFactory
        return store.proceed(
            action.apply {
                if (transitionFactory != null) setTransitions(transitionFactory.create(action))
            }
        )
    }

    companion object
}