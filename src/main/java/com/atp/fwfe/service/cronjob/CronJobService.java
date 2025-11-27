package com.atp.fwfe.service.cronjob;

import com.atp.fwfe.model.account.Account;
import com.atp.fwfe.model.work.WorkPosted;
import com.atp.fwfe.service.account.AccService;
import com.atp.fwfe.service.mailer.MailService;
import com.atp.fwfe.service.work.WorkPostedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.sendgrid.*;
import java.io.IOException;


import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CronJobService {

    private final WorkPostedService workPostedService;
    private final AccService accService;
    private final MailService mailService;

    @Scheduled(cron = "0 0 */3 * * *") // 3 tiếng gửi 1 lần danh sách công việc mới về mail user
    public void notifyNewJobs() {
        try {
            log.info("Đang gửi email công việc mới...");

            List<WorkPosted> newJobs = workPostedService.findUnnotified();
            if (newJobs.isEmpty()) {
                log.info("✅ Không có công việc mới.");
                return;
            }

            List<Account> accounts = accService.findAll();
            int sentCount = 0;

            for (Account account : accounts) {
                String email = account.getEmail();
                if (isValidEmail(email)) {
                    try {
                        mailService.sendNewJobNotification(email, newJobs);
                        sentCount++;
                        log.info("📧 Đã gửi cho: {}", email);
                    } catch (IOException e) {
                        log.error("❌ Gửi lỗi tới {}: {}", email, e.getMessage());
                    }
                }
            }

            workPostedService.markAsNotified(newJobs.stream().map(WorkPosted::getId).toList());

            log.info("✅ Đã gửi email công việc mới đến {} người dùng hợp lệ.", sentCount);

        } catch (Exception e) {
            log.error("❌ Lỗi toàn cục trong notifyNewJobs(): ", e);
        }
    }


    @Scheduled(cron = "0 0 8 * * 6") // Thứ 7 lúc 8:00
    public void sendWeeklyThanks() {
        log.info("Đang gửi email cảm ơn cuối tuần...");
        List<Account> accounts = accService.findAll();
        int sent = 0;
        for(Account account : accounts){
            String email = account.getEmail();
            String name = account.getName();
            if(isValidEmail(email)) {
                try {
                    mailService.sendWeeklyThanks(email, name);
                    sent++;
                } catch (IOException e) { 
                    log.error("❌ Gửi lỗi tới {}: {}", email, e.getMessage());
                }
            }
        }
        log.info("✅ Đã gửi email cảm ơn tới {} người dùng hợp lệ.", sent);
    }

    private boolean isValidEmail(String email){
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

}
