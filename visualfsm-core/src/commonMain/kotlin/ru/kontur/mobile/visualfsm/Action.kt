package ru.kontur.mobile.visualfsm

import ru.kontur.mobile.visualfsm.backStack.BackStackStrategy
import ru.kontur.mobile.visualfsm.backStack.BackStateStack
import ru.kontur.mobile.visualfsm.backStack.StateWithId

/**
 * Is an input object for the State machine.
 * The [action][Action] chooses [transition][Transition] and performs it
 */
abstract class Action<STATE : State> {

    private var transitions: List<Transition<out STATE, out STATE>>? = null

    /** This method is needed to use it in the generated code. Do not use it. */
    fun setTransitions(transitions: List<Transition<out STATE, out STATE>>) {
        this.transitions = transitions
    }

    /**
     * Returns instances of all [transitions][Transition] declared inside this [Action]
     *
     * @return instances of all [transitions][Transition] declared inside this [Action]
     */
    @Deprecated(
        message = "Deprecated, because now the list of transitions is formed in the generated code (of TransitionsFactory).\n" +
                "Code generation not configured or configured incorrectly.\n" +
                "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).",
    )
    open fun getTransitions(): List<Transition<out STATE, out STATE>> {
        return transitions ?: error(
            "\nCode generation not configured or configured incorrectly.\n" +
                    "See the quickstart file for more information on set up code generation (https://github.com/Kontur-Mobile/VisualFSM/blob/main/docs/Quickstart.md).\n"
        )
    }

    /**
     * Selects and starts a [transition][Transition].
     * Calls [transition callbacks][TransitionCallbacks] methods.
     *
     * @param oldState current [state][State]
     * @param oldStateId current [state][State] id
     * @param callbacks [transition callbacks][TransitionCallbacks]
     * @param stateDependencyManager state dependency manager [StateDependencyManager]
     * @param backStateStack back state stack[BackStateStack]
     * @return [new state][State]
     */
    internal fun run(
        oldState: STATE,
        oldStateId: Int,
        callbacks: TransitionCallbacks<STATE>?,
        stateDependencyManager: StateDependencyManager<STATE>?,
        backStateStack: BackStateStack<STATE>
    ): STATE {
        callbacks?.onActionLaunched(this, oldState)

        val availableTransitions = getAvailableTransitions(oldState, backStateStack)

        if (availableTransitions.size > 1) {
            callbacks?.onMultipleTransitionError(this, oldState)
        }

        val selectedTransition = availableTransitions.firstOrNull()

        if (selectedTransition == null) {
            callbacks?.onNoTransitionError(this, oldState)
            return oldState
        }

        callbacks?.onTransitionSelected(this, selectedTransition, oldState)

        val nextState = getNextState(
            oldState,
            selectedTransition,
            backStateStack,
            stateDependencyManager,
            callbacks
        )

        pushNewStateToStackIfNeed(
            oldStateId,
            oldState,
            nextState,
            selectedTransition,
            backStateStack,
            stateDependencyManager
        )

        callbacks?.onNewStateReduced(this, selectedTransition, oldState, nextState)

        return nextState
    }

    private fun pushNewStateToStackIfNeed(
        oldStateId: Int,
        oldState: STATE,
        nextState: STATE,
        selectedTransition: Transition<STATE, STATE>,
        backStateStack: BackStateStack<STATE>,
        stateDependencyManager: StateDependencyManager<STATE>?,
    ) {
        if ((selectedTransition.backStackStrategy == BackStackStrategy.ADD
                    || selectedTransition.backStackStrategy == BackStackStrategy.NEW_ROOT)
            && oldState != nextState
        ) {
            backStateStack.pushAndGetRemoved(
                StateWithId(oldStateId + 1, oldState),
                selectedTransition.backStackStrategy == BackStackStrategy.NEW_ROOT
            ).forEach {
                stateDependencyManager?.removeDependencyForState(it.id, it.state)
            }
        } else {
            stateDependencyManager?.removeDependencyForState(oldStateId, oldState)
        }
    }

    private fun getNextState(
        oldState: STATE,
        selectedTransition: Transition<STATE, STATE>,
        backStateStack: BackStateStack<STATE>,
        stateDependencyManager: StateDependencyManager<STATE>?,
        callbacks: TransitionCallbacks<STATE>?,
    ): STATE {
        val nextState = if (selectedTransition is TransitionBack) {
            val peekResult = backStateStack.peek(selectedTransition.toState)
            if (peekResult == null) {
                callbacks?.onNoStateInBackStackError(selectedTransition.toState, oldState)
                oldState
            } else {
                val (stateFromBackStack, removedStates) = backStateStack.popAndGetRemoved(selectedTransition.toState)
                removedStates.forEach {
                    stateDependencyManager?.removeDependencyForState(it.id, it.state)
                }
                selectedTransition.transform(oldState, stateFromBackStack.state)
            }
        } else {
            selectedTransition.transform(oldState)
        }

        return nextState
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun getAvailableTransitions(
        oldState: STATE,
        backStateStack: BackStateStack<STATE>
    ): List<Transition<STATE, STATE>> {
        return (getTransitions() as List<Transition<STATE, STATE>>).filter {
            isCorrectTransition(
                it,
                oldState,
                backStateStack
            )
        }
    }


    private fun isCorrectTransition(
        transition: Transition<STATE, STATE>,
        oldState: STATE,
        backStateStack: BackStateStack<STATE>,
    ): Boolean {
        return if (transition is TransitionBack) {
            val backState = backStateStack.peek(transition.toState)?.state
            (transition.fromState == oldState::class) && transition.predicate(oldState, backState)
        } else {
            (transition.fromState == oldState::class) && transition.predicate(oldState)
        }
    }
}