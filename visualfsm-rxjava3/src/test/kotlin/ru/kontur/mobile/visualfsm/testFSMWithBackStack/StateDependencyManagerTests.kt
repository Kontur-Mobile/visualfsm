package ru.kontur.mobile.visualfsm.testFSMWithBackStack

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.StateDependencyManager
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSAsyncWorker
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSFeature
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.TestFSMWBSState
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Close
import ru.kontur.mobile.visualfsm.testFSMWithBackStack.fsm.action.Start
import kotlin.reflect.KClass

class StateDependencyManagerTests {

    @Test
    fun stateDependencyTest() {
        val stateDependencyInited: MutableList<Pair<Int, KClass<out TestFSMWBSState>>> = mutableListOf()
        val stateDependencyRemoved: MutableList<Pair<Int, KClass<out TestFSMWBSState>>> = mutableListOf()


        val feature = TestFSMWBSFeature(
            initialState = TestFSMWBSState.Initial,
            asyncWorker = TestFSMWBSAsyncWorker(),
            stateDependencyManager = object : StateDependencyManager<TestFSMWBSState> {
                override fun initDependencyForState(id: Int, state: TestFSMWBSState) {
                    stateDependencyInited.add(id to state::class)
                }

                override fun removeDependencyForState(id: Int, state: TestFSMWBSState) {
                    stateDependencyRemoved.add(id to state::class)
                }
            })

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 1))

        assertEquals(TestFSMWBSState.Async("async1", 1), feature.getCurrentState())

        Thread.sleep(100)

        assertEquals(TestFSMWBSState.Complete("async1"), feature.getCurrentState())

        feature.proceed(Close())

        assertEquals(TestFSMWBSState.Initial, feature.getCurrentState())

        assertTrue(stateDependencyInited.containsAll(listOf(
            0 to TestFSMWBSState.Initial::class,
            1 to TestFSMWBSState.Async::class,
            2 to TestFSMWBSState.Complete::class,
            )))

        assertTrue(stateDependencyRemoved.containsAll(listOf(
            1 to TestFSMWBSState.Async::class,
            2 to TestFSMWBSState.Complete::class,
        )))
    }
}