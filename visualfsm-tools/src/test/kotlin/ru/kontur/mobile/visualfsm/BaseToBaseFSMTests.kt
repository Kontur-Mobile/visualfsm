package ru.kontur.mobile.visualfsm

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.testFSMs.baseToBaseActionState.BaseToBaseActionFSMState
import ru.kontur.mobile.visualfsm.testFSMs.baseToBaseActionState.BaseToBaseStateAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class BaseToBaseFSMTests {
    @Test
    fun generateDigraph() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = BaseToBaseStateAction::class,
            baseState = BaseToBaseActionFSMState::class,
            initialState = BaseToBaseActionFSMState.Initial::class
        )
        println(digraph)
        Assertions.assertEquals(
            "digraph BaseToBaseActionFSMStateTransitions {\n" +
                    "\"Initial\" []\n" +
                    "\"AsyncWorkerState.Remote.LoadingFirst\" []\n" +
                    "\"AsyncWorkerState.Remote.LoadingSecond\" []\n" +
                    "\"Navigation.NextScreen\" []\n" +
                    "\"Initial\" -> \"Initial\" [label=\" BaseToBase \"]\n" +
                    "\"Initial\" -> \"AsyncWorkerState.Remote.LoadingFirst\" [label=\" BaseToBase \"]\n" +
                    "\"Initial\" -> \"AsyncWorkerState.Remote.LoadingSecond\" [label=\" BaseToBase \"]\n" +
                    "\"Initial\" -> \"Navigation.NextScreen\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingFirst\" -> \"Initial\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingFirst\" -> \"AsyncWorkerState.Remote.LoadingFirst\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingFirst\" -> \"AsyncWorkerState.Remote.LoadingSecond\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingFirst\" -> \"Navigation.NextScreen\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingSecond\" -> \"Initial\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingSecond\" -> \"AsyncWorkerState.Remote.LoadingFirst\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingSecond\" -> \"AsyncWorkerState.Remote.LoadingSecond\" [label=\" BaseToBase \"]\n" +
                    "\"AsyncWorkerState.Remote.LoadingSecond\" -> \"Navigation.NextScreen\" [label=\" BaseToBase \"]\n" +
                    "\"Navigation.NextScreen\" -> \"Initial\" [label=\" BaseToBase \"]\n" +
                    "\"Navigation.NextScreen\" -> \"AsyncWorkerState.Remote.LoadingFirst\" [label=\" BaseToBase \"]\n" +
                    "\"Navigation.NextScreen\" -> \"AsyncWorkerState.Remote.LoadingSecond\" [label=\" BaseToBase \"]\n" +
                    "\"Navigation.NextScreen\" -> \"Navigation.NextScreen\" [label=\" BaseToBase \"]\n" +
                    "}\n", digraph
        )
    }
}