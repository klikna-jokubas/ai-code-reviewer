package com.jokubas.aicodereviewer.service;

public record Issue (
    String severity,
    String type,
    Integer line,
    String message
){
}
