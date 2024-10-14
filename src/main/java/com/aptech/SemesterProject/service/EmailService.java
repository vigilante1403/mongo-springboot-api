package com.aptech.SemesterProject.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface EmailService {
    void sendSimpleMessage(
            String to, String subject, String text);
    void sendMessageWithAttachment(
            String to, String subject, String text, String pathToAttachment) throws MessagingException;
    void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String,Object> templateModel,String thymeleafFilename) throws MessagingException, UnsupportedEncodingException;
    void sendMessageUsingThymeleafTemplateToMultiple(String to, String subject, Map<String,Object> templateModel, String thymeleafFilename, List<String> recipients) throws MessagingException, UnsupportedEncodingException;
}
