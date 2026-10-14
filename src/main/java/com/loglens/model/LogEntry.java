package com.loglens.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String level;
    private final String message;
    private final int lineNumber;

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

    public String getFormattedTimestamp() { return timestamp.format(DISPLAY); }
}
