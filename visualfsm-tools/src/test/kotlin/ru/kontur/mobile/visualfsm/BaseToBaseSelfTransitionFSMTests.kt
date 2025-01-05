package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMs.baseToBaseSelfTransitionActionState.BaseToBaseSelfTransitionActionFSMState
import ru.kontur.mobile.visualfsm.testFSMs.baseToBaseSelfTransitionActionState.BaseToBaseSelfTransitionStateAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class BaseToBaseSelfTransitionFSMTests {
    @Test
    fun generateDigraph() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = BaseToBaseSelfTransitionStateAction::class,
            baseState = BaseToBaseSelfTransitionActionFSMState::class,
            initialState = BaseToBaseSelfTransitionActionFSMState.Initial::class
        )
        println(digraph)
        Assertions.assertEquals(
            "digraph BaseToBaseSelfTransitionActionFSMStateTransitions {\n" +
                    "\"Initial\" []\n" +
                    "\"AsyncWorkerState.Cache.LoadingFirst\" [ color=red]\n" +
                    "\"AsyncWorkerState.Cache.LoadingSecond\" [ color=red]\n" +
                    "\"AsyncWorkerState.Loading\" [ color=red]\n" +
                    "\"AsyncWorkerState.Remote.LoadingFirst\" [ color=red]\n" +
                    "\"AsyncWorkerState.Remote.LoadingSecond\" [ color=red]\n" +
                    "\"Navigation.BackScreen\" [ color=red]\n" +
                    "\"Navigation.NextScreen\" [ color=red]\n" +
                    "\"Initial\" -> \"Initial\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingFirst\" -> \"AsyncWorkerState.Remote.LoadingFirst\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingSecond\" -> \"AsyncWorkerState.Remote.LoadingSecond\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Cache.LoadingFirst\" -> \"AsyncWorkerState.Cache.LoadingFirst\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Cache.LoadingSecond\" -> \"AsyncWorkerState.Cache.LoadingSecond\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Loading\" -> \"AsyncWorkerState.Loading\" [label=\" BaseToBase \"]\n" +
                    "\"Navigation.NextScreen\" -> \"Navigation.NextScreen\" [label=\" BaseToBase \"]\n" +
                    "\"Navigation.BackScreen\" -> \"Navigation.BackScreen\" [label=\" BaseToBase \"]\n" +
                    "}\n", digraph
        )
    }
}