package ru.kontur.mobile.visualfsm.transitioncallbacks

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State
import ru.kontur.mobile.visualfsm.Transition
import ru.kontur.mobile.visualfsm.TransitionCallbacks
import ru.kontur.mobile.visualfsm.log.LogFormatter
import ru.kontur.mobile.visualfsm.log.Logger
import ru.kontur.mobile.visualfsm.log.LoggerMode
import ru.kontur.mobile.visualfsm.log.LoggerMode.*

class LogTransitionCallbacks<STATE : State, ACTION : Action<STATE>>(
    private val loggerMode: LoggerMode,
    private val logger: Logger,
    private val tag: String,
    private val logFormatter: LogFormatter<STATE, ACTION>,
) : TransitionCallbacks<STATE, ACTION> {

    override fun onInitialStateReceived(initialState: STATE) {
        when (loggerMode) {
            NONE,
            ERRORS -> Unit

            INFO,
            VERBOSE -> logger.log(
                tag = tag,
                message = "LogTransitionCallbacks onInitialStateReceived: ${logFormatter.stateFormatter(initialState)}"
            )
        }
    }

    override fun onActionLaunched(
        action: ACTION,
        currentState: STATE
    ) {
        when (loggerMode) {
            NONE,
            ERRORS -> Unit

            INFO,
            VERBOSE -> logger.log(
                tag = tag,
                message = "onActionLaunched: ${logFormatter.actionFormatter(action)} " +
                        "from state ${currentState::class.simpleName}"
            )
        }
    }

    override fun onTransitionSelected(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        currentState: STATE
    ) {
        when (loggerMode) {
            NONE,
            ERRORS,
            INFO -> Unit

            VERBOSE -> logger.log(
                tag = tag,
                message = "onTransitionSelected: ${transition::class.simpleName}, " +
                        "for action ${action::class.simpleName}"
            )
        }
    }

    override fun onNewStateReduced(
        action: ACTION,
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        newState: STATE
    ) {
        when (loggerMode) {
            NONE,
            ERRORS -> Unit

            INFO,
            VERBOSE -> logger.log(
                tag = tag,
                message = "onNewStateReduced: ${logFormatter.stateFormatter(newState)}"
            )
        }
    }

    override fun onNoTransitionError(
        action: ACTION,
        currentState: STATE,
    ) {
        if (loggerMode == NONE) return

        logger.error(
            tag = tag,
            message = "NoTransitionError for ${logFormatter.actionFormatter(action)} " +
                    "from ${logFormatter.stateFormatter(currentState)}",
            errorGroupId = "to${action::class.simpleName}" +
                    "from${currentState::class.simpleName}"
        )
    }

    override fun onMultipleTransitionError(
        action: ACTION,
        currentState: STATE,
        suitableTransitions: List<Transition<STATE, STATE>>
    ) {
        if (loggerMode == NONE) return

        logger.error(
            tag = tag,
            message = "MultipleTransitionError for ${logFormatter.actionFormatter(action)} " +
                    "from ${logFormatter.stateFormatter(currentState)}, " +
                    "suitableTransitions [${
                        suitableTransitions.map { it::class.simpleName }.joinToString()
                    }]",
            errorGroupId = "suitable${suitableTransitions.size}" +
                    "to${action::class.simpleName}" +
                    "from${currentState::class.simpleName}"
        )
    }
}
