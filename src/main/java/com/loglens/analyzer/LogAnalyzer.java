package com.loglens.analyzer;

import com.loglens.model.AnalysisResult;
import com.loglens.model.LogEntry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Does the counting. Knows nothing about files or web: it only gets a list of entries.
 * That makes it very easy to unit test.
 */
@Component
public class LogAnalyzer {

    public AnalysisResult analyze(List<LogEntry> entries) {
        AnalysisResult result = new AnalysisResult();
        result.setEntries(entries);

        // HashMap = O(1) lookup, so counting N errors takes one pass: O(N)
        Map<String, Integer> errorCounts = new HashMap<>();

        for (LogEntry entry : entries) {
            switch (entry.getLevel()) {
                case "INFO" -> result.setInfoCount(result.getInfoCount() + 1);
                case "WARN" -> result.setWarnCount(result.getWarnCount() + 1);
                case "DEBUG" -> result.setDebugCount(result.getDebugCount() + 1);
                case "ERROR" -> {
                    result.setErrorCount(result.getErrorCount() + 1);
                    // merge: if key is new put 1, otherwise add 1 to the existing count
                    errorCounts.merge(entry.getMessage(), 1, Integer::sum);
                }
                default -> { /* parser only produces the 4 levels above */ }
            }
        }

        // Keep only messages seen 2+ times, sorted with the biggest count first.
        // LinkedHashMap remembers insertion order, so the sorted order survives.
        Map<String, Integer> repeated = new LinkedHashMap<>();
        errorCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> repeated.put(e.getKey(), e.getValue()));
        result.setRepeatedErrors(repeated);

        return result;
    }
}
