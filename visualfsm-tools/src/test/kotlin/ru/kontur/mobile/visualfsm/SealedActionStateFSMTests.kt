package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.SealedActionStateFSMState
import ru.kontur.mobile.visualfsm.testFSMs.sealedActionState.actions.BaseSealedStateFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class SealedActionStateFSMTests {
    @Test
    fun generateDigraph() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = BaseSealedStateFSMAction::class,
            baseState = SealedActionStateFSMState::class,
            initialState = SealedActionStateFSMState.Initial::class
        )
        println(digraph)
        Assertions.assertEquals(
            "digraph SealedActionStateFSMStateTransitions {\n" +
                    "\"Initial\" []\n" +
                    "\"AsyncWorkerState.LoadingFirst\" []\n" +
                    "\"AsyncWorkerState.LoadingSecond\" []\n" +
                    "\"Navigation.Back\" []\n" +
                    "\"Navigation.Screen.NextFirstScreen\" []\n" +
                    "\"Navigation.Screen.NextSecondScreen\" []\n" +
                    "\"Initial\" -> \"Navigation.Back\" [label=\" InitialToBack \"]\n" +
                    "\"AsyncWorkerState.LoadingFirst\" -> \"Navigation.Back\" [label=\" AsyncWorkerStateToBack \"]\n" +
                    "\"AsyncWorkerState.LoadingSecond\" -> \"Navigation.Back\" [label=\" AsyncWorkerStateToBack \"]\n" +
                    "\"Navigation.Screen.NextFirstScreen\" -> \"Initial\" [label=\" NavigateCompletedToInitial \"]\n" +
                    "\"Navigation.Screen.NextSecondScreen\" -> \"Initial\" [label=\" NavigateCompletedToInitial \"]\n" +
                    "\"Navigation.Screen.NextFirstScreen\" -> \"AsyncWorkerState.LoadingFirst\" [label=\" NavigateCompletedToAsyncWorker \"]\n" +
                    "\"Navigation.Screen.NextFirstScreen\" -> \"AsyncWorkerState.LoadingSecond\" [label=\" NavigateCompletedToAsyncWorker \"]\n" +
                    "\"Navigation.Screen.NextSecondScreen\" -> \"AsyncWorkerState.LoadingFirst\" [label=\" NavigateCompletedToAsyncWorker \"]\n" +
                    "\"Navigation.Screen.NextSecondScreen\" -> \"AsyncWorkerState.LoadingSecond\" [label=\" NavigateCompletedToAsyncWorker \"]\n" +
                    "\"Initial\" -> \"Navigation.Screen.NextFirstScreen\" [label=\" InitialFirstScreen \"]\n" +
                    "\"AsyncWorkerState.LoadingFirst\" -> \"Navigation.Screen.NextFirstScreen\" [label=\" AsyncWorkerStateFirstScreen \"]\n" +
                    "\"AsyncWorkerState.LoadingSecond\" -> \"Navigation.Screen.NextFirstScreen\" [label=\" AsyncWorkerStateFirstScreen \"]\n" +
                    "\"Initial\" -> \"Navigation.Screen.NextSecondScreen\" [label=\" InitialToSecondScreen \"]\n" +
                    "\"AsyncWorkerState.LoadingFirst\" -> \"Navigation.Screen.NextSecondScreen\" [label=\" AsyncWorkerStateToSecondScreen \"]\n" +
                    "\"AsyncWorkerState.LoadingSecond\" -> \"Navigation.Screen.NextSecondScreen\" [label=\" AsyncWorkerStateToSecondScreen \"]\n" +
                    "\"Initial\" -> \"Initial\" [label=\" ChangeByObserve \"]\n" +
                    "\"AsyncWorkerState.LoadingFirst\" -> \"AsyncWorkerState.LoadingFirst\" [label=\" ChangeByObserve \"]\n" +
                    "\"AsyncWorkerState.LoadingSecond\" -> \"AsyncWorkerState.LoadingSecond\" [label=\" ChangeByObserve \"]\n" +
                    "\"Navigation.Back\" -> \"Navigation.Back\" [label=\" ChangeByObserve \"]\n" +
                    "\"Navigation.Screen.NextFirstScreen\" -> \"Navigation.Screen.NextFirstScreen\" [label=\" ChangeByObserve \"]\n" +
                    "\"Navigation.Screen.NextSecondScreen\" -> \"Navigation.Screen.NextSecondScreen\" [label=\" ChangeByObserve \"]\n" +
                    "\"Initial\" -> \"AsyncWorkerState.LoadingFirst\" [label=\" InitialToFirstLoading \"]\n" +
                    "\"AsyncWorkerState.LoadingFirst\" -> \"AsyncWorkerState.LoadingSecond\" [label=\" InitialToSecondLoading \"]\n" +
                    "}\n", digraph
        )
    }
}