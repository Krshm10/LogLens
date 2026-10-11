package com.loglens.parser;

import com.loglens.model.LogEntry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    void parsesValidLine() {
        LogEntry e = parser.parseLine("2026-10-11 10:00:05 ERROR Database connection failed", 5);

        assertThat(e).isNotNull();
        assertThat(e.getLevel()).isEqualTo("ERROR");
        assertThat(e.getMessage()).isEqualTo("Database connection failed");
        assertThat(e.getLineNumber()).isEqualTo(5);
        assertThat(e.getFormattedTimestamp()).isEqualTo("2026-10-11 10:00:05");
    }

    @Test
    void returnsNullForMalformedLine() {
        assertThat(parser.parseLine("hello this is not a log", 1)).isNull();
    }

    @Test
    void returnsNullForUnknownLevel() {
        assertThat(parser.parseLine("2026-10-11 10:00:05 FATAL boom", 1)).isNull();
    }

    @Test
    void returnsNullForImpossibleDate() {
        assertThat(parser.parseLine("2026-13-45 10:00:05 ERROR bad date", 1)).isNull();
    }

    @Test
    void returnsNullForNullInput() {
        assertThat(parser.parseLine(null, 1)).isNull();
    }
}
