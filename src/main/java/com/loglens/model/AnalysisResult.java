package com.loglens.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The final report for one uploaded file. Mostly a data holder.
 */
public class AnalysisResult {

    private int totalLines; // non-blank lines in the file
    private int unrecognizedLines; // lines that did not match our format
    private int infoCount;
    private int warnCount;
    private int errorCount;
    private int debugCount;
    private List<LogEntry> entries = new ArrayList<>();
    // error message -> how many times it appeared (only messages seen 2+ times),
    // biggest first
    private Map<String, Integer> repeatedErrors = new LinkedHashMap<>();

    /** Number of lines that were successfully parsed. */
    public int getTotalEntries() {
        return entries.size();
    }

    public int getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }

    public int getUnrecognizedLines() {
        return unrecognizedLines;
    }

    public void setUnrecognizedLines(int unrecognizedLines) {
        this.unrecognizedLines = unrecognizedLines;
    }

    public int getInfoCount() {
        return infoCount;
    }

    public void setInfoCount(int infoCount) {
        this.infoCount = infoCount;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(int warnCount) {
        this.warnCount = warnCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getDebugCount() {
        return debugCount;
    }

    public void setDebugCount(int debugCount) {
        this.debugCount = debugCount;
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LogEntry> entries) {
        this.entries = entries;
    }

    public Map<String, Integer> getRepeatedErrors() {
        return repeatedErrors;
    }

    public void setRepeatedErrors(Map<String, Integer> repeatedErrors) {
        this.repeatedErrors = repeatedErrors;
    }

    /** One plain-English sentence shown at the top of the results page. */
    public String getHeadline() {
        if (errorCount == 0) {
            if (warnCount == 0) {
                return "No errors or warnings. This file looks healthy.";
            }
            return "No errors, but " + warnCount + (warnCount == 1 ? " warning" : " warnings") + " worth a look.";
        }
        return errorCount + (errorCount == 1 ? " error" : " errors") + " in " + getTotalEntries() + " log lines.";
    }

    /**
     * A short pointer on where to start looking. A lead, never a claimed root
     * cause.
     */
    public String getHint() {
        if (errorCount == 0) {
            return "";
        }
        if (repeatedErrors.isEmpty()) {
            return "Every error appeared only once, so start with the earliest one.";
        }
        Map.Entry<String, Integer> top = repeatedErrors.entrySet().iterator().next();
        return "\u201c" + top.getKey() + "\u201d happened " + top.getValue()
                + " times. Start there. It's a lead, not proof of the root cause.";
    }
}