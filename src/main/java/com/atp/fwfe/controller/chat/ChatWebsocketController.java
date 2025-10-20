package com.atp.fwfe.controller.chat;

import com.atp.fwfe.dto.chat.ChatMessageRequest;
import com.atp.fwfe.dto.chat.ChatMessageResponse;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.chat.ChatMessage;
import com.atp.fwfe.service.chat.ChatMessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatWebsocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService messageService;

    @MessageMapping("/chat.group")
    public void sendGroupMessage(@Valid ChatMessageRequest request, Principal principal) {
        String sender = principal.getName(); // đã được WebSocketSecurityConfig giải mã từ JWT

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setType(ChatMessage.MessageType.GROUP);

        ChatMessage saved = messageService.save(message);
        ChatMessageResponse response = mapToResponse(saved, sender);
        if (response.getReadByUsers() == null) {
            response.setReadByUsers(new ArrayList<>());
        }

        messagingTemplate.convertAndSend("/topic/chat/group", response);
    }

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Valid ChatMessageRequest request, Principal principal) {
        String sender = principal.getName();

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(request.getReceiver());
        message.setContent(request.getContent());
        message.setType(ChatMessage.MessageType.PRIVATE);

        ChatMessage saved = messageService.save(message);
        ChatMessageResponse response = mapToResponse(saved, sender);

        messagingTemplate.convertAndSendToUser(
                request.getReceiver(),
                "/queue/messages",
                response
        );
    }

    @MessageMapping("/chat.markRead")
    public void markAsRead(Long messageId, Principal principal) {
        ChatMessage updated = messageService.markAsRead(messageId, principal.getName());

        ChatMessageResponse response = mapToResponse(updated, principal.getName());

        messagingTemplate.convertAndSend("/topic/chat/group", response);

        String sender   = updated.getSender();
        String receiver = updated.getReceiver();
        if (receiver != null) {
            messagingTemplate.convertAndSendToUser(
                    receiver, "/queue/messages", response);
        }
        messagingTemplate.convertAndSendToUser(
                sender,   "/queue/messages", response);
    }


    @MessageMapping("/chat.send")
    public void testPrincipal(ChatMessageRequest request, Principal principal) {
        System.out.println("🔐 [TEST] Người dùng WebSocket: " + principal.getName());
    }

    private ChatMessageResponse mapToResponse(ChatMessage message, String currentUsername) {
        List<String> readByUsers = message.getReadBy().stream()
                .map(Account::getUsername)
                .collect(Collectors.toList());

        return new ChatMessageResponse(
                message.getId(),
                message.getSender(),
                message.getReceiver(),
                message.getContent(),
                message.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                message.getType().toString(),
                readByUsers,
                message.getSender().equals(currentUsername)
        );
    }

}
