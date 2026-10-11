package com.loglens.service;

import com.loglens.analyzer.LogAnalyzer;
import com.loglens.model.AnalysisResult;
import com.loglens.parser.LogParser;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogAnalysisServiceTest {

    private final LogAnalysisService service = new LogAnalysisService(new LogParser(), new LogAnalyzer());

    private MockMultipartFile file(String name, String content) {
        return new MockMultipartFile("file", name, "text/plain", content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void analyzesValidFileAndSkipsBadLines() throws Exception {
        String content = """
                2026-10-11 10:00:01 INFO Started
                garbage line

                2026-10-11 10:00:05 ERROR Boom
                2026-10-11 10:00:06 ERROR Boom
                """;

        AnalysisResult r = service.analyzeFile(file("app.log", content));

        assertThat(r.getTotalLines()).isEqualTo(4);        // blank line ignored
        assertThat(r.getTotalEntries()).isEqualTo(3);
        assertThat(r.getUnrecognizedLines()).isEqualTo(1);
        assertThat(r.getRepeatedErrors()).containsEntry("Boom", 2);
    }

    @Test
    void rejectsEmptyFile() {
        assertThatThrownBy(() -> service.analyzeFile(file("app.log", "")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void rejectsWrongExtension() {
        assertThatThrownBy(() -> service.analyzeFile(file("virus.exe", "2026-10-11 10:00:01 INFO hi")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported");
    }

    @Test
    void rejectsFileWithNoRecognizableLines() {
        assertThatThrownBy(() -> service.analyzeFile(file("app.log", "just some text\nmore text")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No recognizable");
    }
}
