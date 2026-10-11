package com.loglens.service;

import com.loglens.analyzer.LogAnalyzer;
import com.loglens.model.AnalysisResult;
import com.loglens.model.LogEntry;
import com.loglens.parser.LogParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the steps: validate -> read lines -> parse -> analyze.
 * Business rules live here, not in the controller.
 */
@Service
public class LogAnalysisService {

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5 MB

    private final LogParser parser;
    private final LogAnalyzer analyzer;

    // Constructor injection: Spring passes in the parser and analyzer for us.
    // (Preferred over field @Autowired because the dependencies are explicit and easy to mock in tests.)
    public LogAnalysisService(LogParser parser, LogAnalyzer analyzer) {
        this.parser = parser;
        this.analyzer = analyzer;
    }

    /**
     * @throws IllegalArgumentException with a user-friendly message if the file is not acceptable
     */
    public AnalysisResult analyzeFile(MultipartFile file) throws IOException {
        validate(file);

        List<LogEntry> entries = new ArrayList<>();
        int totalLines = 0;
        int unrecognized = 0;

        // We only READ the file as text. It is never executed or saved to disk.
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue; // empty lines are not "unrecognized", just ignored
                }
                totalLines++;
                LogEntry entry = parser.parseLine(line, lineNumber);
                if (entry == null) {
                    unrecognized++;
                } else {
                    entries.add(entry);
                }
            }
        }

        if (entries.isEmpty()) {
            throw new IllegalArgumentException(
                    "No recognizable log lines found. Expected format: "
                            + "2026-10-11 10:00:05 ERROR Your message here");
        }

        AnalysisResult result = analyzer.analyze(entries);
        result.setTotalLines(totalLines);
        result.setUnrecognizedLines(unrecognized);
        return result;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty. Please choose a .log or .txt file.");
        }
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!name.endsWith(".log") && !name.endsWith(".txt")) {
            throw new IllegalArgumentException("Unsupported file type. Please upload a .log or .txt file.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("File is too large. The maximum size is 5 MB.");
        }
    }
}
