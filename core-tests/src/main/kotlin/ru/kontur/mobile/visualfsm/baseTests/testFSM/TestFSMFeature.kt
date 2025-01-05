package ru.kontur.mobile.visualfsm.baseTests.testFSM

import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.baseTests.testFSM.action.TestFSMAction
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory

@GenerateTransitionsFactory
class TestFSMFeature(
    initialState: TestFSMState,
    private val asyncWorker: AsyncWorker<TestFSMState, TestFSMAction>? = null,
) : Feature<TestFSMState, TestFSMAction>(
    initialState = initialState,
    asyncWorker = asyncWorker,
    transitionCallbacks = listOf(TestFSMTransitionCallbacks()),
    transitionsFactory = provideTransitionsFactory(),
) {
    fun onDestroy() {
        asyncWorker?.unbind()
    }
}