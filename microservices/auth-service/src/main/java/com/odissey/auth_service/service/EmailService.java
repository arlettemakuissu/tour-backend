package com.odissey.auth_service.service;

import com.odissey.auth_service.dto.request.GenericMail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;


    private final JavaMailSender javaMailSender;



    public void sendMail (GenericMail genericMail) throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

    helper.setFrom(new InternetAddress(sender));
    helper.setTo(genericMail.getTo());
    helper.setSubject(genericMail.getSubject());
    helper.setText(genericMail.getBody());

    javaMailSender.send(mimeMessage);

    }
}
