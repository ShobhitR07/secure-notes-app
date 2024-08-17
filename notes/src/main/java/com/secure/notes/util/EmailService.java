package com.secure.notes.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to,String resetUrl){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("password reset email");
        message.setText("Click the link to reset your password:"+resetUrl);
        mailSender.send(message);
    }


}
