package ru.kontur.mobile.visualfsm.sealedTests.testFSM

import ru.kontur.mobile.visualfsm.AsyncWorker
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.GenerateTransitionsFactory
import ru.kontur.mobile.visualfsm.baseTests.testFSM.TestFSMTransitionCallbacks
import ru.kontur.mobile.visualfsm.providers.GeneratedTransitionsFactoryProvider.provideTransitionsFactory
import ru.kontur.mobile.visualfsm.sealedTests.testFSM.action.TestFSMAction

@GenerateTransitionsFactory
class TestFSMFeature(
    initialState: TestFSMState,
    asyncWorker: AsyncWorker<TestFSMState, TestFSMAction>? = null,
) : Feature<TestFSMState, TestFSMAction>(
    initialState = initialState,
    asyncWorker = asyncWorker,
    transitionCallbacks = listOf(TestFSMTransitionCallbacks()),
    transitionsFactory = provideTransitionsFactory(),
)