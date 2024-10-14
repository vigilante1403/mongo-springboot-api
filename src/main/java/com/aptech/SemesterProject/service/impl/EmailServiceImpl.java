package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;
    @Autowired
    public EmailServiceImpl(SpringTemplateEngine thymeleafTemplateEngine){
        this.thymeleafTemplateEngine=thymeleafTemplateEngine;
    }
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("new.vytruong.1812@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setFrom("new.vytruong.1812@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("Content",file);
        emailSender.send(message);
    }

    @Override
    public void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel,String thymeleafFilename) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setFrom("m61uoyni4@mozmail.com","Vina Travel Team");
        helper.setTo(to);
        helper.setSubject(subject);

        Context thymeleafContext = new Context();
        templateModel.forEach((key,value)->{
            thymeleafContext.setVariable(key,value);
        });
        String htmlBody = thymeleafTemplateEngine.process(thymeleafFilename,thymeleafContext);
        helper.setText(htmlBody,true);
        emailSender.send(message);


    }

    @Override
    public void sendMessageUsingThymeleafTemplateToMultiple(String to, String subject, Map<String, Object> templateModel, String thymeleafFilename, List<String> bccRecipients) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setFrom("Vina Travel team <m61uoyni4@mozmail.com>");
        helper.setSubject(subject);
        if (bccRecipients != null && !bccRecipients.isEmpty()) {
            helper.setBcc(bccRecipients.toArray(new String[0]));
        }
        Context thymeleafContext = new Context();
        templateModel.forEach((key,value)->{
            thymeleafContext.setVariable(key,value);
        });
        String htmlBody = thymeleafTemplateEngine.process(thymeleafFilename,thymeleafContext);
        helper.setText(htmlBody,true);
        emailSender.send(message);
    }
}
