package ru.kontur.mobile.visualfsm.testFSM.action

import ru.kontur.mobile.visualfsm.TransitionBack
import ru.kontur.mobile.visualfsm.testFSM.TestFSMState

class Cancel : TestFSMAction() {

    inner class Cancel : TransitionBack<TestFSMState.Async, TestFSMState.Initial>()
}