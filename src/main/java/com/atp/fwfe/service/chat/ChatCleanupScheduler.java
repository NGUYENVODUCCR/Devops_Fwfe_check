package com.atp.fwfe.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.atp.fwfe.service.chat.ChatMessageService;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCleanupScheduler {

    private final ChatMessageService chatMessageService;

    @Scheduled(cron = "0 0 * * * *") 
    public void scheduledCleanMessages() {
        try {
            chatMessageService.cleanOldMessages();
        } catch (Exception e) {
            log.error("❌ Lỗi khi dọn tin nhắn", e);
        }
    }
}
