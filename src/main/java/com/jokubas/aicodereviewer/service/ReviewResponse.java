package com.jokubas.aicodereviewer.service;

import java.util.List;

public record ReviewResponse (
        List<Issue> issues
){
}
