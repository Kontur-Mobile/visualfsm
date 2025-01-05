package ru.kontur.mobile.visualfsm.multiStageAsyncTests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.Start
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.rx.TestFSMAsyncWorkerRx
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.rx.TestFSMFeatureRx

class ExecuteIfNotExistWithSameClassTests {

    @Test
    fun doNotInterruptMultistageAsyncTaskTest() {
        val feature = TestFSMFeatureRx(
            initialState = TestFSMState.Initial,
            asyncWorker = TestFSMAsyncWorkerRx(),
        )

        val testObserver = feature.observeState().test()

        assertEquals(TestFSMState.Initial, feature.getCurrentState())

        feature.proceed(Start("async1", 10))

        testObserver.awaitCount(6)

        testObserver.assertValues(
            TestFSMState.Initial,
            TestFSMState.AsyncWithStage("async1", "stage0", 10),
            TestFSMState.AsyncWithStage("async1", "stage1", 10),
            TestFSMState.AsyncWithStage("async1", "stage2", 10),
            TestFSMState.AsyncWithStage("async1", "stage3", 10),
            TestFSMState.Complete("async1")
        )

        testObserver.dispose()
    }
}