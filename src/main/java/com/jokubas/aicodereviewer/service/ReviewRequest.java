package com.jokubas.aicodereviewer.service;

public record ReviewRequest(
        String code,
        String language
) {
}
