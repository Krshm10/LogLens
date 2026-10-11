package com.loglens.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Catches "file too big" errors thrown by Spring BEFORE our controller method runs
 * (the 5MB limit in application.properties), and shows a friendly message instead of an error page.
 */
@ControllerAdvice
public class UploadErrorHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleTooBig(Model model) {
        model.addAttribute("error", "File is too large. The maximum size is 5 MB.");
        return "index";
    }
}
