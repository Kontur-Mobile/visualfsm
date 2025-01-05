package ru.kontur.mobile.visualfsm.log

interface Logger {
    fun log(tag: String, message: String)

    fun error(tag: String, message: String, errorGroupId: String)
}
