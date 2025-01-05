package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Observable
import ru.kontur.mobile.visualfsm.*
import ru.kontur.mobile.visualfsm.log.LogParams
import ru.kontur.mobile.visualfsm.log.LoggerMode

/**
 * Is the facade for FSM. Provides access to subscription on [state][State] changes
 * and [proceed] method to execute [actions][Action]
 *
 * @param stateSource the [state source][IStateSourceRx] for storing and subscribing to state,
 * can be external to implement a common state tree between parent and child state machines
 * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
 * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
 * on provided event calls (like logging, debugging, or metrics) (optional)
 * @param transitionsFactory a function that returns a [TransitionsFactory] instance to create the transition list
 * for the action
 * @param logParams the internal logger params, by default configured for write only error messages
 * to [ru.kontur.mobile.visualfsm.log.StdoutLogger]
 * and format actions and states by [ru.kontur.mobile.visualfsm.log.DefaultVerboseLogFormatter]
 */
open class FeatureRx<STATE : State, ACTION : Action<STATE>>(
    stateSource: IStateSourceRx<STATE>,
    asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
    transitionCallbacks: List<TransitionCallbacks<STATE, ACTION>> = listOf(),
    transitionsFactory: FeatureRx<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
    logParams: LogParams<STATE, ACTION> = LogParams(loggerMode = LoggerMode.ERRORS)
) : BaseFeature<STATE, ACTION>() {

    private val transitionsFactory: TransitionsFactory<STATE, ACTION> = transitionsFactory(this)

    private val store: StoreRx<STATE, ACTION> = StoreRx(
        stateSource = stateSource,
        transitionCallbacks = getTransitionCallbacksAggregator(logParams, transitionCallbacks)
    )

    init {
        asyncWorker?.bind(this)
    }

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
     * on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     */
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: List<TransitionCallbacks<STATE, ACTION>> = listOf(),
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
    ) : this(RootStateSourceRx(initialState), asyncWorker, transitionCallbacks, { transitionsFactory })

    /**
     * @param initialState initial [state][State]
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
     * on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a function that returns a [TransitionsFactory] instance to create the transition list
     * for the action
     */
    constructor(
        initialState: STATE,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: List<TransitionCallbacks<STATE, ACTION>> = listOf(),
        transitionsFactory: FeatureRx<STATE, ACTION>.() -> TransitionsFactory<STATE, ACTION>,
    ) : this(RootStateSourceRx(initialState), asyncWorker, transitionCallbacks, transitionsFactory)

    /**
     * @param stateSource the [state source][IStateSourceRx] for storing and subscribing to state,
     * can be external to implement a common state tree between parent and child state machines
     * @param asyncWorker [AsyncWorkerRx] instance for manage state-based asynchronous tasks (optional)
     * @param transitionCallbacks the [callbacks][TransitionCallbacks] for declare third party logic
     * on provided event calls (like logging, debugging, or metrics) (optional)
     * @param transitionsFactory a [TransitionsFactory] instance to create the transition list for the action
     */
    constructor(
        stateSource: IStateSourceRx<STATE>,
        asyncWorker: AsyncWorkerRx<STATE, ACTION>? = null,
        transitionCallbacks: List<TransitionCallbacks<STATE, ACTION>> = listOf(),
        transitionsFactory: TransitionsFactory<STATE, ACTION>,
    ) : this(stateSource, asyncWorker, transitionCallbacks, { transitionsFactory })

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
            return store.proceed(action, transitionsFactory)
        }
    }
}