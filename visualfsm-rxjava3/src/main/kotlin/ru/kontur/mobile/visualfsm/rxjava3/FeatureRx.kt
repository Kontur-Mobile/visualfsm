package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Observable
import ru.kontur.mobile.visualfsm.*

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param initialState initial [state][State]
 * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
 * @param transitionsFactory a function that returns a [TransitionsFactory] instance to create the transition list for the action
 */
open class FeatureRx<STATE : State, ACTION : Action<STATE>>(
    initialState: STATE,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
    transitionCallbacks: TransitionCallbacks<STATE>? = null,
    transitionsFactory: FeatureRx<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
    stateKeeper: IStateKeeperRx<STATE> = OwnStateKeeperRx(initialState)
) : BaseFeature<STATE, ACTION>() {

    private var transitionsFactory: TransitionsFactory<STATE, ACTION>? = null

    private val store = StoreRx<STATE, ACTION>(stateKeeper, transitionCallbacks)

    init {
        this.transitionsFactory = transitionsFactory(this)
        asyncWorker?.bind(this)
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     */
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: TransitionCallbacks<STATE>? = null,
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
        stateKeeper: IStateKeeperRx<STATE> = OwnStateKeeperRx(initialState)
    ) : this(initialState, asyncWorker, transitionCallbacks, { transitionsFactory }, stateKeeper)

    /**
     * Provides a [observable][Observable] of [states][State]
     *
     * @return a [observable][Observable] of [states][State]
     */
    fun observeState(): Observable<STATE> {
        return store.observeState().distinctUntilChanged()
    }

    /**
     * Provides a [observable][Observable] of [states][State] without
     * distinctUntilChanged(), for AsyncWorkerRx
     *
     * @return a [observable][Observable] of [states][State]
     */
    internal fun observeAllStates(): Observable<STATE> {
        return store.observeState()
    }

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    override fun getCurrentState(): STATE {
        return store.getCurrentState()
    }

    /**
     * Submits an [action][Action] to be executed to the [store][StoreRx]
     *
     * @param action [Action] to run
     */
    override fun proceed(action: ACTION) {
        synchronized(this) {
            val transitionsFactory = this.transitionsFactory
            return store.proceed(
                action.apply {
                    if (transitionsFactory != null) setTransitions(transitionsFactory.create(action))
                }
            )
        }
    }
}