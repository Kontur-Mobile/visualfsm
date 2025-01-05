package ru.kontur.mobile.visualfsm.log

enum class LoggerMode {
    NONE, // Disable internal logger
    ERRORS, // Log only errors
    INFO, // Log errors and info
    VERBOSE; // Log all log messages
}