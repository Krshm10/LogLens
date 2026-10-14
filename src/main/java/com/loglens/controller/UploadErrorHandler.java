package com.loglens.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class UploadErrorHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleTooBig(Model model) {
        model.addAttribute("error", "File is too large. The maximum size is 5 MB.");
        return "index";
    }
}
