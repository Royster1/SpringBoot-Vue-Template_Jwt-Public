package com.example.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@RabbitListener(queues = "mail")
@Component
public class MailQueueListener {
    @Resource
    JavaMailSender sender;

    @Value("$spring.mail.username")
    String username;

    // 根据消息发送邮件
    public void sendMailMessage(Map<String, Object> data) {
        String email = (String) data.get("email ");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type) {
            case "register" -> createMessage("欢迎注册我们的网站",
                    "您的邮箱注册验证码为: " + code + ",有效时间3分钟,为了保障您的安全,请勿泄露验证码信息", email);
            case "reset" -> createMessage("您的密码重置邮件",
                    "您好,您正在进行重置密码操作,验证码为: " + code + ",有效时间3分钟,如非本人操作,请无视", email);
            default -> null;
        };
        if (message == null) return;
        sender.send(message);
    }


    private SimpleMailMessage createMessage(String title, String content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }

}
