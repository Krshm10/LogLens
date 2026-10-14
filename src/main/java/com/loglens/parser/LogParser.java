package com.loglens.parser;

import com.loglens.model.LogEntry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogParser {

    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+(INFO|WARN|ERROR|DEBUG)\\s+(.*)$");

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
            return null;
        }
    }
}
