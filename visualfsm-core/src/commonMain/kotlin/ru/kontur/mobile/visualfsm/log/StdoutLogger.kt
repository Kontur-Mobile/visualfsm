package ru.kontur.mobile.visualfsm.log

class StdoutLogger : Logger {
    override fun log(tag: String, message: String) {
        println("StdoutFSMLogger $tag: $message")
    }

    override fun error(tag: String, message: String, errorGroupId: String) {
        println("StdoutFSMLogger $tag e: $message")
        println("StdoutFSMLogger $tag errorGroupId = $errorGroupId")
    }
}
