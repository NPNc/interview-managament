package com.example.be_service.dto;

import java.util.ArrayList;
import java.util.List;

public record ErrorDTO(String statusCode, String title, String detail, List<String> fieldErrors) {
    public ErrorDTO(String statusCode, String title, String detail){
        this(statusCode, title, detail, new ArrayList<String>());
    }
}