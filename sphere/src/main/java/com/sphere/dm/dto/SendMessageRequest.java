package com.sphere.dm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotNull(message = "Receiver username is required")
    private String receiverUsername;

    @NotBlank(message = "Message content is required")
    private String content;
}