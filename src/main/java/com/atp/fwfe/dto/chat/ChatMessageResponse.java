package com.atp.fwfe.dto.chat;

import com.atp.fwfe.model.chat.ChatMessage;
import com.atp.fwfe.model.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {

    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private String timestamp;
    private String type;
    private List<String> readByUsers;

    private boolean isSender;

    public boolean isSender() {
        return isSender;
    }

    public void setIsSender(boolean senderFlag) {
        this.isSender = senderFlag;
    }

    public static ChatMessageResponse fromEntity(ChatMessage entity, String currentUsername) {
        ChatMessageResponse dto = new ChatMessageResponse();

        dto.setId(entity.getId());
        dto.setSender(entity.getSender());
        dto.setReceiver(entity.getReceiver());
        dto.setContent(entity.getContent());

        dto.setTimestamp(
                entity.getTimestamp() != null
                        ? entity.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : null
        );

        dto.setType(entity.getType().name());

        dto.setReadByUsers(
                entity.getReadBy().stream()
                        .map(Account::getUsername)
                        .collect(Collectors.toList())
        );

        dto.setIsSender(currentUsername != null && currentUsername.equals(entity.getSender()));


        return dto;
    }
}
