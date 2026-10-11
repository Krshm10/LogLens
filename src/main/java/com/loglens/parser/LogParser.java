package com.loglens.parser;

import com.loglens.model.LogEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns ONE line of text into a LogEntry.
 * Supported format:  2026-10-11 10:00:05 ERROR Database connection failed
 */
@Component
public class LogParser {

    // group 1 = timestamp, group 2 = level, group 3 = message
    // Compiled once (static final) because compiling a regex is slower than using it.
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+(INFO|WARN|ERROR|DEBUG)\\s+(.*)$");

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * @return a LogEntry, or null if the line is not in the supported format.
     *         Returning null (not throwing) means one bad line never stops the whole analysis.
     */
    public LogEntry parseLine(String line, int lineNumber) {
        if (line == null) {
            return null;
        }
        Matcher matcher = LINE_PATTERN.matcher(line.trim());
        if (!matcher.matches()) {
            return null;
        }
        try {
            LocalDateTime time = LocalDateTime.parse(matcher.group(1), TIMESTAMP);
            return new LogEntry(time, matcher.group(2), matcher.group(3).trim(), lineNumber);
        } catch (DateTimeParseException e) {
            // Looks like a date but isn't a real one (e.g. month 13) -> treat as unrecognized
            return null;
        }
    }
}
