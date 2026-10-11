# LogLens - Interview Notes (WHAT / WHY / DON'T)

## 30-second pitch
"LogLens is a Java log analyzer built with Spring Boot. You upload a log file; a parser extracts
timestamp, level and message using a regex; an analyzer counts severities and groups repeated
errors with a HashMap. I used a layered architecture (controller, service, parser/analyzer) and
processed files in memory, so no database was needed in version 1. I wrote JUnit tests for the
parser, analyzer and service."

## WHAT each piece is, and WHY I chose it
- **Log file**: text an app writes about what happened (start-up, warnings, errors) so developers can debug after the fact.
- **Spring Boot**: gives an embedded Tomcat server, auto-configuration and dependency injection, so I write business code instead of server setup.
- **Thymeleaf**: server-side HTML templates. Simple, no separate frontend project; it escapes text by default (`th:text`), which protects against XSS from log content.
- **Layered architecture**: each layer has one job, so I can change the UI without touching parsing, and unit test parsing without a web server.
- **Controller vs Service**: controller = HTTP in/out (which page, which status). Service = the actual workflow and rules (validation, reading, parse, analyze).
- **Regex in LogParser**: a log line has a fixed shape; three capture groups give timestamp, level, message. `Pattern` is `static final` so it compiles once.
- **HashMap for repeated errors**: one pass over the data, O(1) per update with `merge(msg, 1, Integer::sum)`, so total O(n). `LinkedHashMap` keeps the sorted-by-count order.
- **Constructor injection**: dependencies are explicit and easy to replace in tests (I just do `new LogAnalysisService(new LogParser(), new LogAnalyzer())`).
- **Parser returns null instead of throwing**: a bad line is normal input, not an exceptional event. One bad line must not stop the analysis.
- **No database**: version 1 doesn't need history. Fewer moving parts = easier to explain and test.
- **JUnit tests**: parser (valid, malformed, bad date, null), analyzer (counts, grouping), service (empty file, wrong extension, no valid lines).
- **ControllerAdvice**: Spring rejects files over 5 MB before my controller runs, so a global handler shows a friendly message.

## HOW I handle invalid input
Empty file -> message. Wrong extension -> message. >5 MB -> rejected twice (Spring property and a check in the service).
Malformed lines -> skipped and counted. No valid lines at all -> message instead of an empty dashboard.

## Security (what I did / didn't do)
- DO: validate extension and size, treat the file only as text, escape output, hide stack traces (`server.error.include-stacktrace=never`, generic error text).
- DON'T: execute, save, or trust the uploaded content.
- Honest gap: I check the file *name* extension, not its real content type.

## DON'T claim (be honest, it earns trust)
- Don't say it does live monitoring, AI/root-cause detection, or handles huge production logs.
- Say "repeated errors are a good place to start investigating", not "this is the root cause".

## Likely questions - short answers
1. **What is a log file?** A record of events from a running app, with time, severity and message.
2. **Why Spring Boot?** Embedded server, auto-config, dependency injection, very common in industry.
3. **Controller vs service?** See above.
4. **How does regex help?** Describes the line structure once; groups pull out the 3 fields.
5. **Why HashMap?** Fast counting by key in one pass.
6. **Limitations?** One format, multi-line stack traces unrecognized, in-memory, exact-match grouping.
7. **How to scale?** Stream line by line (don't keep all entries), pagination, background job processing, store results in a DB, size limits per user.
8. **Why not a database?** Not needed yet; I'd add one for history/multiple users.
9. **How did you test?** JUnit unit tests for parser/analyzer/service plus a sample log with duplicates and bad lines.
10. **What would you improve first?** Support Logback/Log4j formats and stack traces, then message normalization (replace numbers/IDs so "User 12 not found" and "User 15 not found" group together).
