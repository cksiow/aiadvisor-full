package com.universal.core.library.email;


import lombok.SneakyThrows;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

    @SneakyThrows
    public static void sendEmail(String userName, String password, String smtpHost
            , String from, String to, String cc, String subject, String body) {


        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", smtpHost);

        // SSL Port
        properties.put("mail.smtp.port", "587");

        // enable authentication
        properties.put("mail.smtp.auth", "true");

        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.user", userName);
        properties.put("mail.smtp.password", password);
        // SSL Factory
        properties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");

        // creating Session instance referenced to
        // Authenticator object to pass in
        // Session.getInstance argument
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    // override the getPasswordAuthentication
                    // method
                    protected PasswordAuthentication
                    getPasswordAuthentication() {
                        return new PasswordAuthentication(userName,
                                password);
                    }
                });


        // javax.mail.internet.MimeMessage class is mostly
        // used for abstraction.
        MimeMessage message = new MimeMessage(session);

        // header field of the header.
        message.setFrom(new InternetAddress(userName, from));
        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(to));
        if (!cc.isBlank()) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
        }


        message.setSubject(subject);
        message.setContent(body, "text/html");

        // Send message
        Transport.send(message);

    }
}
