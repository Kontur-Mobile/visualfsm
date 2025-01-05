package ru.kontur.mobile.visualfsm.sealedTests

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.helper.runFSMFeatureTest
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMFeature
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.NavigateToBack
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.NavigateToNext
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.ObserveChange
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.ShowDialog
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.tools.VisualFSM

class SealedClassInActionTests {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doSealedFromStateActionTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(
                initialState = TestFSMState.Initial(0),
                asyncWorker = TestFSMAsyncWorker(dispatcher),
            )

        }
    ) { feature, states ->
        feature.proceed(ObserveChange(1))
        advanceUntilIdle()
        feature.proceed(NavigateToBack())
        advanceUntilIdle()
        feature.proceed(NavigateToNext())
        advanceUntilIdle()
        feature.proceed(ShowDialog())
        advanceUntilIdle()
        feature.proceed(ObserveChange(2))
        advanceUntilIdle()
        feature.proceed(ObserveChange(3))
        advanceUntilIdle()
        assertEquals(
            listOf(
                TestFSMState.Initial(0),
                TestFSMState.Initial(1),
                TestFSMState.NavigationState.Screen.Back(1),
                TestFSMState.Initial(1),
                TestFSMState.NavigationState.Screen.Next(1),
                TestFSMState.Initial(1),
                TestFSMState.NavigationState.DialogState.Show(1),
                TestFSMState.NavigationState.DialogState.Show(2),
                TestFSMState.NavigationState.DialogState.Show(3),
            ),
            states
        )
    }

    @Test
    fun generateDigraphTest() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class
        )
        println(digraph)
        assertEquals(
            "digraph TestFSMStateTransitions {\n" +
                    "\"Initial\" []\n" +
                    "\"NavigationState.DialogState.Hide\" []\n" +
                    "\"NavigationState.DialogState.Show\" []\n" +
                    "\"NavigationState.Screen.Back\" []\n" +
                    "\"NavigationState.Screen.Next\" []\n" +
                    "\"NavigationState.DialogState.Show\" -> \"NavigationState.DialogState.Hide\" [label=\" HideDialog \"]\n" +
                    "\"NavigationState.Screen.Back\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
                    "\"NavigationState.Screen.Next\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
                    "\"NavigationState.DialogState.Show\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
                    "\"NavigationState.DialogState.Hide\" -> \"Initial\" [label=\" NavigateCompleted \"]\n" +
                    "\"Initial\" -> \"NavigationState.Screen.Back\" [label=\" NavigateBack \"]\n" +
                    "\"Initial\" -> \"NavigationState.Screen.Next\" [label=\" NavigateNext \"]\n" +
                    "\"Initial\" -> \"Initial\" [label=\" ObserveChange \"]\n" +
                    "\"NavigationState.DialogState.Show\" -> \"NavigationState.DialogState.Show\" [label=\" ObserveChange \"]\n" +
                    "\"NavigationState.DialogState.Hide\" -> \"NavigationState.DialogState.Hide\" [label=\" ObserveChange \"]\n" +
                    "\"Initial\" -> \"NavigationState.DialogState.Show\" [label=\" ShowDialog \"]\n" +
                    "}\n", digraph
        )
    }
}