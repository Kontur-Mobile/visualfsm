package ru.kontur.mobile.visualfsm

import ru.kontur.mobile.visualfsm.log.LogParams
import ru.kontur.mobile.visualfsm.log.LoggerMode
import ru.kontur.mobile.visualfsm.transitioncallbacks.LogTransitionCallbacks
import ru.kontur.mobile.visualfsm.transitioncallbacks.TransitionCallbacksAggregator

abstract class BaseFeature<STATE : State, ACTION : Action<STATE>> {

    /**
     * Returns current state
     *
     * @return current [state][State]
     */
    abstract fun getCurrentState(): STATE

    /**
     * Submits an [action][Action] to be executed to the [store][Store]
     *
     * @param action [Action] to run
     */
    abstract fun proceed(action: ACTION)

    /**
     * Transition callbacks with logging if internal logging enabled
     * @return transition callbacks aggregator
     */
    protected fun getTransitionCallbacksAggregator(
        logParams: LogParams<STATE, ACTION>,
        transitionCallbacksList: List<TransitionCallbacks<STATE, ACTION>>
    ): TransitionCallbacksAggregator<STATE, ACTION> {

        val newTransitionCallbacksList = if (logParams.loggerMode != LoggerMode.NONE) {
            val logTransitionCallbacks = LogTransitionCallbacks(
                loggerMode = logParams.loggerMode,
                logger = logParams.logger,
                tag = logParams.tag ?: this::class.simpleName ?: "Feature",
                logFormatter = logParams.logFormatter,
            )
            listOf(logTransitionCallbacks) + transitionCallbacksList
        } else {
            transitionCallbacksList
        }

        return TransitionCallbacksAggregator(newTransitionCallbacksList)
    }

    companion object
}