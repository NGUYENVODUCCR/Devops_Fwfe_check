package com.atp.fwfe.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ChatMessageRequest {

    @NotBlank
    private String content;

    private String receiver;

    @Pattern(regexp = "(?i)PRIVATE|GROUP", message = "Loại tin nhắn không hợp lệ!")
    private String type;

    private LocalDateTime timestamp;

}
