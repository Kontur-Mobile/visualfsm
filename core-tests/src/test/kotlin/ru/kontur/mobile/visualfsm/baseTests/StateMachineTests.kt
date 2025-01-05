package ru.kontur.mobile.visualfsm.baseTests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMAsyncWorker
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMFeature
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.Cancel
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.Start
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.helper.runFSMFeatureTest
import ru.kontur.mobile.visualfsm.tools.VisualFSM
import ru.kontur.mobile.visualfsm.tools.graphviz.DotAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.EdgeAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.GraphAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.NodeAttributes
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.ArrowHead
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.Color
import ru.kontur.mobile.visualfsm.tools.graphviz.enums.NodeShape

@OptIn(ExperimentalCoroutinesApi::class)
class StateMachineTests {
    @Test
    fun generateDigraphTest() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class
        )

        assertEquals(
            "digraph TestFSMStateTransitions {\n" +
                    "\"Initial\" []\n" +
                    "\"Async\" []\n" +
                    "\"Complete\" []\n" +
                    "\"Error\" []\n" +
                    "\"Async\" -> \"Initial\" [label=\" Cancel \"]\n" +
                    "\"Async\" -> \"Complete\" [label=\" Success \"]\n" +
                    "\"Async\" -> \"Error\" [label=\" Error \"]\n" +
                    "\"Initial\" -> \"Async\" [label=\" Start \"]\n" +
                    "\"Async\" -> \"Async\" [label=\" StartOther \"]\n" +
                    "}\n", digraph
        )
    }

    @Test
    fun generateDigraphWithAttributesTest() {
        val digraph = VisualFSM.generateDigraph(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class,
            attributes = DotAttributes(
                graphAttributes = GraphAttributes(raw = "rankdir=LR;\nsize=\"8,5\""),
                nodeAttributes = { state ->
                    if (state == TestFSMState.Async::class) {
                        NodeAttributes(
                            shape = NodeShape.Octagon,
                            color = Color.Blue,
                            fontColor = Color.White,
                            raw = "style=filled"
                        )
                    } else {
                        NodeAttributes(
                            shape = NodeShape.Box,
                            color = Color.Black,
                            fontColor = Color.Black
                        )
                    }
                },
                edgeAttributes = { from, _ ->
                    if (from == TestFSMState.Async::class) {
                        EdgeAttributes(
                            arrowHead = ArrowHead.Empty,
                            color = Color.Blue,
                            fontColor = Color.DarkGreen,
                            raw = "color=darkgreen"
                        )
                    } else EdgeAttributes()
                }
            )
        )

        assertEquals(
            "digraph TestFSMStateTransitions {\n" +
                    "rankdir=LR;\n" +
                    "size=\"8,5\"\n" +
                    "\"Initial\" [ shape=box]\n" +
                    "\"Async\" [ color=blue fontcolor=white shape=octagon style=filled]\n" +
                    "\"Complete\" [ shape=box]\n" +
                    "\"Error\" [ shape=box]\n" +
                    "\"Async\" -> \"Initial\" [label=\" Cancel \" color=blue fontcolor=darkgreen arrowhead=empty color=darkgreen]\n" +
                    "\"Async\" -> \"Complete\" [label=\" Success \" color=blue fontcolor=darkgreen arrowhead=empty color=darkgreen]\n" +
                    "\"Async\" -> \"Error\" [label=\" Error \" color=blue fontcolor=darkgreen arrowhead=empty color=darkgreen]\n" +
                    "\"Initial\" -> \"Async\" [label=\" Start \"]\n" +
                    "\"Async\" -> \"Async\" [label=\" StartOther \" color=blue fontcolor=darkgreen arrowhead=empty color=darkgreen]\n" +
                    "}\n", digraph
        )
    }

    @Test
    fun allStatesReachableTest() {
        val notReachableStates = VisualFSM.getUnreachableStates(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
            initialState = TestFSMState.Initial::class
        )

        assertTrue(
            notReachableStates.isEmpty(),
            "FSM have unreachable states: ${notReachableStates.joinToString(", ")}"
        )
    }

    @Test
    fun oneFinalStateTest() {
        val finalStates = VisualFSM.getFinalStates(
            baseAction = TestFSMAction::class,
            baseState = TestFSMState::class,
        )

        assertTrue(
            finalStates.size == 2 && finalStates.containsAll(
                listOf(
                    TestFSMState.Complete::class,
                    TestFSMState.Error::class
                )
            ),
            "FSM have not correct final states: ${finalStates.joinToString(", ")}"
        )
    }

    @Test
    fun startAsyncTest() {
        val feature = TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(StandardTestDispatcher()))
        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))
        assertEquals(TestFSMState.Async("async1", 1), feature.getCurrentState())
    }

    @Test
    fun restartAsyncTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(dispatcher))
        }
    ) { feature, states ->
        feature.proceed(Start("async1", 1000, "1"))
        advanceTimeBy(500)
        feature.proceed(Start("async1", 1000, "2"))
        advanceUntilIdle()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 1000),
                TestFSMState.Complete("async1", "2") // expected salt "2",
                // because internal SharedFlow does not have a distinctUntilChange
                // and first AsyncState was cancelled by ExecuteAndCancelExist task type
            ),
            states
        )

        assertEquals(
            "1", // expected salt "1", because external StateFlow has a distinctUntilChange
            (states[1] as TestFSMState.Async).salt
        )
    }

    @Test
    fun endAsyncTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(dispatcher))
        }
    ) { feature, states ->
        feature.proceed(Start("async1", 1000))
        advanceUntilIdle()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 1000),
                TestFSMState.Complete("async1")
            ),
            states
        )
    }

    @Test
    fun errorAsyncTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(dispatcher))
        }
    ) { feature, states ->
        feature.proceed(Start("error", 1000))
        advanceUntilIdle()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("error", 1000),
                TestFSMState.Error
            ),
            states
        )
    }

    @Test
    fun cancelAsyncTest() = runFSMFeatureTest(
        featureFactory = { dispatcher ->
            TestFSMFeature(TestFSMState.Initial, TestFSMAsyncWorker(dispatcher))
        }
    ) { feature, states ->
        feature.proceed(Start("async1", 100))
        advanceTimeBy(50)

        // Cancel task
        feature.proceed(Cancel())
        advanceUntilIdle()

        // Start new
        feature.proceed(Start("async2", 150))
        advanceUntilIdle()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 100),
                TestFSMState.Initial,
                TestFSMState.Async("async2", 150),
                TestFSMState.Complete("async2")
            ),
            states
        )
    }

    @Test
    fun destroyFeatureAsyncCancellationTest() {
        var exceptionHandled = false

        runFSMFeatureTest(
            featureFactory = { dispatcher ->
                TestFSMFeature(
                    TestFSMState.Initial,
                    TestFSMAsyncWorker(dispatcher, onSubscriptionError = {
                        exceptionHandled = true
                    })
                )
            }
        ) { feature, states ->
            feature.proceed(Start("async1", 100))
            advanceTimeBy(50)

            feature.onDestroy()
            advanceUntilIdle()

            assertEquals(
                listOf(
                    TestFSMState.Initial,
                    TestFSMState.Async("async1", 100)
                ),
                states
            )
        }
        assertEquals(false, exceptionHandled)
    }

    @Test
    fun stateSubscriptionErrorHandlerTest() {
        var exceptionHandled = false

        runFSMFeatureTest(
            featureFactory = { dispatcher ->
                TestFSMFeature(
                    TestFSMState.Initial,
                    TestFSMAsyncWorker(dispatcher, onSubscriptionError = {
                        exceptionHandled = true
                    })
                )
            }
        ) { feature, states ->
            feature.proceed(Start("uncaught error", 1000))
            advanceUntilIdle()

            assertEquals(
                listOf(
                    TestFSMState.Initial,
                    TestFSMState.Async("uncaught error", 1000)
                ),
                states
            )
        }
        assertEquals(true, exceptionHandled)
    }

    @Test
    fun multiplyCancelByStartOtherAsyncTest() {
        for (i in 1..100) {
            println("Step: $i")
            cancelByStartOtherAsyncTest()
        }
    }

    private fun cancelByStartOtherAsyncTest() = runTest(UnconfinedTestDispatcher()) {
        val feature = TestFSMFeature(
            initialState = TestFSMState.Initial,
            asyncWorker = TestFSMAsyncWorker(Dispatchers.Default),
        )

        val states = mutableListOf<TestFSMState>()

        val job = async {
            feature.observeState().take(7).collect {
                states.add(it)
            }
        }

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 20))
        Thread.sleep(10)
        feature.proceed(Start("async2", 20))
        Thread.sleep(10)
        feature.proceed(Start("async3", 20))
        Thread.sleep(10)
        feature.proceed(Start("async4", 20))
        Thread.sleep(10)
        feature.proceed(Start("async5", 20))

        assertEquals(TestFSMState.Async("async5", 20), feature.getCurrentState())

        job.await()

        assertEquals(
            listOf(
                TestFSMState.Initial,
                TestFSMState.Async("async1", 20),
                TestFSMState.Async("async2", 20),
                TestFSMState.Async("async3", 20),
                TestFSMState.Async("async4", 20),
                TestFSMState.Async("async5", 20),
                TestFSMState.Complete("async5")
            ),
            states
        )
    }
}