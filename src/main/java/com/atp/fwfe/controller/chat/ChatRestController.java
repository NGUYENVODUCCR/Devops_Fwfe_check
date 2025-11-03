package com.atp.fwfe.controller.chat;

import com.atp.fwfe.dto.chat.ChatMessageResponse;
import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.service.chat.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {
        "https://fwfe.duckdns.org",
        "http://152.42.196.211:3000",
        "http://10.0.2.2:8000",
        "http://127.0.0.1:8000",
        "http://localhost:*"
}, allowCredentials = "true")
public class ChatRestController {

    @Autowired
    private ChatMessageService messageService;

    @GetMapping("/received")
    public List<ChatMessageResponse> getReceivedMessages(Principal principal) {
        return messageService.findReceivedMessages(principal.getName())
                .stream()
                .map(m -> new ChatMessageResponse(
                        m.getId(),
                        m.getSender(),
                        m.getReceiver(),
                        m.getContent(),
                        m.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        m.getType().toString(),
                        (m.getReadBy() == null || m.getReadBy().isEmpty())
                                ? new ArrayList<>() // fallback: tránh null
                                : m.getReadBy().stream().map(Account::getUsername).toList(),
                        false
                ))
                .toList();
    }


    @PutMapping("/mark-read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Principal principal) {
        messageService.markAsRead(id, principal.getName());
        return ResponseEntity.ok("Đã đánh dấu đã đọc!");
    }

    @GetMapping("/chat/history/private")
    public List<ChatMessageResponse> getPrivateChatHistory(
            @RequestParam String user,
            @RequestParam(defaultValue = "50") int limit,
            Principal principal) {

        String currentUser = principal.getName();
        return  messageService.getPrivateChatHistory(currentUser, user, limit);
    }

    @GetMapping("/chat/private/inbox")
    public ResponseEntity<List<String>> getPrivateInbox(@RequestParam("myUsername") String myUsername) {
        System.out.println("📥 Lấy hộp thư cho: " + myUsername);
        List<String> senders = messageService.getSendersTo(myUsername);
        return ResponseEntity.ok(senders);
    }

}
