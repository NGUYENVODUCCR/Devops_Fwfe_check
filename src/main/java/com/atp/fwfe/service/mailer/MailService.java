package com.atp.fwfe.service.mailer;

import com.atp.fwfe.model.work.WorkPosted;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String email, String name) throws IOException {
        String subject = "🎉 Chào mừng bạn đến với hệ thống của chúng tôi!";
        String html = """
               <div style="font-family: Arial, sans-serif; color: #333;">
                  <h2 style="color: #2d8cf0;">👋 Xin chào %s!</h2>
                  <p>Cảm ơn bạn đã đăng ký và trở thành một phần của <strong>cộng đồng việc làm</strong> của chúng tôi! 🌟</p>
                  <hr style="margin: 20px 0;" />
                  <p>Chúc bạn một ngày tuyệt vời và nhiều thành công! 💼</p>
                  <p style="margin-top: 20px;">Trân trọng,<br/><strong>Đội ngũ Hệ thống Việc Làm</strong></p>
               </div>
               """.formatted(name);

        sendHtml(email, subject, html);
    }

    public void sendWeeklyThanks(String email, String name) throws IOException {
        String subject = "Cảm ơn bạn đã luôn đồng hành cùng cộng đồng!";
        String html= """
                 <div style="font-family: Arial, sans-serif; color: #333;">
                    <p>Chúng tôi rất biết ơn sự đồng hành của bạn trong tuần vừa qua.</p>
                    <p>Hẹn gặp lại bạn vào những tuần tới với nhiều cơ hội việc làm hấp dẫn!</p>
                    <p>Chúc bạn ngày cuối tuần an yên bên gia đình và thật tận hưởng hôm nay nhé %s :)</p>
                    <p><strong>Hệ thống Việc Làm</strong></p>
                </div>
                """.formatted(name);

        sendHtml(email, subject, html);
    }

    public void sendNewJobNotification(String email, List<WorkPosted> jobs) throws IOException {
        String subject = "🆕 Việc làm mới dành cho bạn!";
        StringBuilder jobListHtml = new StringBuilder();

        for (WorkPosted job : jobs){
            jobListHtml.append("<li>")
                    .append("<strong>").append("Vị trí tuyển dụng: ").append(job.getPosition()).append("</strong>")
                    .append(" - Mức lương cơ bản: ").append(job.getSalary()).append("₫")
                    .append(" Địa chỉ: ").append(job.getCompany().getAddress())
                    .append("</li>");
        }

        String html = """
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h3>Xin chào!</h3>
                    <p>Dưới đây là một số công việc mới bạn có thể quan tâm:</p>
                    <ul>%s</ul>
                    <p>Hãy truy cập hệ thống để biết thêm chi tiết!</p>
                </div>
                """.formatted(jobListHtml);

        sendHtml(email, subject, html);
    }

    public void sendHtml(String to, String subject, String html) throws IOException {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", html);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        if (response.getStatusCode() >= 400) {
            throw new IOException("SendGrid gửi mail thất bại: " + response.getBody());
        }

        System.out.println("✅ Đã gửi email tới người dùng: " + to);
    }
}
