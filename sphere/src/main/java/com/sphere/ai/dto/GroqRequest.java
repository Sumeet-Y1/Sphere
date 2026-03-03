package com.sphere.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class GroqRequest {
    private String model;
    private List<GroqMessage> messages;
    private int max_tokens;
}