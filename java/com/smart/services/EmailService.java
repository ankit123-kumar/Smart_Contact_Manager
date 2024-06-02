package com.smart.services;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
 
	public boolean sendEmail(String subject, String message, String to) {

		boolean f = false;
		// rest of the code
		// from
		String from = "kumarankit63786@gmail.com";

		// Variable for gmail
		String host = "smtp.gmail.com";

		// get the system properties
		Properties properties = new Properties();
		

		// setting important information to properties object

		// host set	
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		 
		// Set up authenticator
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kumarankit63786@gmail.com", "bymk pncg ibsr lufy");
            }
        };

        // Create a mail session with the authenticator
        Session session = Session.getInstance(properties, authenticator);

		


		// Step 2 : compose the message [text,multi media]
		MimeMessage m= new MimeMessage(session);

		try {

			// from email
			//message.setFrom(from);
             
			m.setFrom(new InternetAddress("sender@example.com"));
			// adding recipient to message
			m.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			// adding subject to message
			m.setSubject(subject);

			// adding text to message
			//m.setText(message);
			m.setContent(message,"text/html");

			// send

			// Step 3 : send the message using Transport class
			Transport.send(m);

			System.out.println("Email sent successfully...................");
			f = true;

		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return f;
	}
}
