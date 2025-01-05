package ru.kontur.mobile.visualfsm.multiStageAsyncTests.rx

import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMTransitionCallbacks
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory
import ru.kontur.mobile.visualfsm.rxjava3.AsyncWorkerRx
import ru.kontur.mobile.visualfsm.rxjava3.FeatureRx
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.TestFSMState
import ru.kontur.mobile.visualfsm.multiStageAsyncTests.testFSM.action.TestFSMAction

@GenerateTransitionsFactory
class TestFSMFeatureRx(
    initialState: TestFSMState,
    asyncWorker: AsyncWorkerRx<TestFSMState, TestFSMAction>? = null,
) : FeatureRx<TestFSMState, TestFSMAction>(
    initialState = initialState,
    asyncWorker = asyncWorker,
    transitionCallbacks = listOf(TestFSMTransitionCallbacks()),
    transitionsFactory = provideTransitionsFactory()
)