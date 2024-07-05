package com.example.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
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

    @Value("${spring.mail.username}")
    String username;

    // 根据消息发送邮件 消费者代码
    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data){
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type){
            case "register" ->
                    createMessage("注册", "您的验证码为：" + code + "有效时间为一分钟，请勿向他人泄露验证码", email);
            case "reset" ->
                    createMessage("重置密码", "您的验证码为：" + code + "有效时间为一分钟，请勿向他人泄露验证码", email);
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
