package com.loglens.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One successfully parsed log line.
 * Plain class with getters (instead of a record) so Thymeleaf can read ${entry.level} easily.
 */
public class LogEntry {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String level;      // INFO, WARN, ERROR or DEBUG
    private final String message;
    private final int lineNumber;    // line in the uploaded file, handy for debugging

    public LogEntry(LocalDateTime timestamp, String level, String message, int lineNumber) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.lineNumber = lineNumber;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getLevel() { return level; }
    public String getMessage() { return message; }
    public int getLineNumber() { return lineNumber; }

    /** Used by the HTML page so we don't need extra Thymeleaf date libraries. */
    public String getFormattedTimestamp() { return timestamp.format(DISPLAY); }
}
