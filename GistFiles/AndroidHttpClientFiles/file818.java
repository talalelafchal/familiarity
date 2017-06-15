package utility;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class SendMail {

        public static void execute(String reportFileName) throws Exception {

            final String username = "Your@Email.Address";
            final String password = "EnterYourPasswordHere";

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();

            Properties props = new Properties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("Your@Email.Address"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("Your@Email.Address"));
                message.setSubject("Automation Report - " + date);
                message.setText("Automation Results");

                MimeBodyPart messageBodyPart = new MimeBodyPart();

                Multipart multipart = new MimeMultipart();

                messageBodyPart = new MimeBodyPart();
                String file = "test-output/";
                String fileName = "emailable-report.html";
                DataSource source = new FileDataSource(file + fileName);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(fileName);
                multipart.addBodyPart(messageBodyPart);

                message.setContent(multipart);
                System.out.println("Sending");
                Transport.send(message);
                System.out.println("Done");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }
