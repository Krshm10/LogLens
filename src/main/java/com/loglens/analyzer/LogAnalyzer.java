package com.loglens.analyzer;

import com.loglens.model.AnalysisResult;
import com.loglens.model.LogEntry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class LogAnalyzer {

    public AnalysisResult analyze(List<LogEntry> entries) {
        AnalysisResult result = new AnalysisResult();
        result.setEntries(entries);

        Map<String, Integer> errorCounts = new HashMap<>();

        for (LogEntry entry : entries) {
            switch (entry.getLevel()) {
                case "INFO" -> result.setInfoCount(result.getInfoCount() + 1);
                case "WARN" -> result.setWarnCount(result.getWarnCount() + 1);
                case "DEBUG" -> result.setDebugCount(result.getDebugCount() + 1);
                case "ERROR" -> {
                    result.setErrorCount(result.getErrorCount() + 1);
                    errorCounts.merge(entry.getMessage(), 1, Integer::sum);
                }
                default -> {}
            }
        }

        Map<String, Integer> repeated = new LinkedHashMap<>();
        errorCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> repeated.put(e.getKey(), e.getValue()));
        result.setRepeatedErrors(repeated);

        return result;
    }
}
