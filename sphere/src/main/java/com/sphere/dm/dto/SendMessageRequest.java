package com.sphere.dm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank(message = "Receiver username is required")
    private String receiverUsername;

    @NotBlank(message = "Message content is required")
    private String content;
}
