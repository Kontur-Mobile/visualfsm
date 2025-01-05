package ru.kontur.mobile.visualfsm.log

import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State

/**
 * @param loggerMode - [LoggerMode] (NONE - for disable internal logging, ERRORS(default) - log errors only, VERBOSE - log all events)
 * @param logger - custom [Logger] implementation, by default used internal [StdoutLogger]
 * @param tag - custom tag for log messages, if null - used Feature class simple name
 * @param logFormatter - custom [LogFormatter] implementation, by default used [DefaultVerboseLogFormatter]
 */
data class LogParams<STATE : State, ACTION : Action<STATE>>(
    val loggerMode: LoggerMode,
    val logger: Logger = StdoutLogger(),
    val tag: String? = null,
    val logFormatter: LogFormatter<STATE, ACTION> = DefaultVerboseLogFormatter(),
)
