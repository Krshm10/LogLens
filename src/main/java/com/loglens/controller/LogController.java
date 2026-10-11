package com.loglens.controller;

import com.loglens.model.AnalysisResult;
import com.loglens.service.LogAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Handles browser requests only. No parsing or counting here - it just calls the service
 * and picks which HTML page to show.
 */
@Controller
public class LogController {

    private final LogAnalysisService service;

    public LogController(LogAnalysisService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String showUploadPage() {
        return "index"; // -> templates/index.html
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("file") MultipartFile file, Model model) {
        try {
            AnalysisResult result = service.analyzeFile(file);
            model.addAttribute("result", result);
            model.addAttribute("fileName", file.getOriginalFilename());
            return "result";
        } catch (IllegalArgumentException e) {
            // our own validation message - safe to show
            model.addAttribute("error", e.getMessage());
        } catch (IOException e) {
            // generic text on purpose: never leak internal details or stack traces
            model.addAttribute("error", "Could not read the file. Please try again.");
        }
        return "index";
    }
}
