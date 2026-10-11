package com.loglens.analyzer;

import com.loglens.model.AnalysisResult;
import com.loglens.model.LogEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogAnalyzerTest {

    private final LogAnalyzer analyzer = new LogAnalyzer();

    private LogEntry entry(String level, String msg) {
        return new LogEntry(LocalDateTime.of(2026, 10, 11, 10, 0, 0), level, msg, 1);
    }

    @Test
    void countsEachLevel() {
        AnalysisResult r = analyzer.analyze(List.of(
                entry("INFO", "a"), entry("INFO", "b"),
                entry("WARN", "c"), entry("ERROR", "d"), entry("DEBUG", "e")));

        assertThat(r.getInfoCount()).isEqualTo(2);
        assertThat(r.getWarnCount()).isEqualTo(1);
        assertThat(r.getErrorCount()).isEqualTo(1);
        assertThat(r.getDebugCount()).isEqualTo(1);
        assertThat(r.getTotalEntries()).isEqualTo(5);
    }

    @Test
    void groupsRepeatedErrorsBiggestFirst() {
        AnalysisResult r = analyzer.analyze(List.of(
                entry("ERROR", "File not found"),
                entry("ERROR", "Database connection failed"),
                entry("ERROR", "Database connection failed"),
                entry("ERROR", "Database connection failed"),
                entry("ERROR", "File not found")));

        assertThat(r.getRepeatedErrors()).containsExactly(
                java.util.Map.entry("Database connection failed", 3),
                java.util.Map.entry("File not found", 2));
    }

    @Test
    void singleErrorIsNotRepeated() {
        AnalysisResult r = analyzer.analyze(List.of(entry("ERROR", "only once")));
        assertThat(r.getRepeatedErrors()).isEmpty();
    }

    @Test
    void onlyErrorsAreGrouped() {
        AnalysisResult r = analyzer.analyze(List.of(entry("WARN", "same"), entry("WARN", "same")));
        assertThat(r.getRepeatedErrors()).isEmpty();
    }
}
