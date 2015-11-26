package Applications.Email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by NIghtCrysIs on 2015/03/14.
 *
 * <code>SendMail</code> Contains static method for sending emails.
 *
 * @author Johnson Wong
 * @since 1.7
 */
public class SendMail {

    /**
     * Method for sending email. Utilizes methods from javax.mail
     * Sets up properties using the provided parameters then sends
     * a message object from a domain to the recipient's email address
     *
     * @see javax.mail
     *
     * @param username final String object, stores the username or email address of the sender
     * @param password final String object, stores the password
     * @param title String object, stores the title of the email
     * @param message String object, stores the contents of the message in HTML format
     * @param to String object, contains the recipient's email address to be sent to
     * @param host String object, contains the host domain address
     * @param port String object, contains the port number used in sending the email
     *
     * @throws javax.mail.MessagingException Thrown by all messaging classes.
     */

    public static void SendMail(final String username, final String password, String title, String message, String to, String host, String port) throws MessagingException{
        //Setup email server
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        //Authenticator and session
        //Needed for server login

        Authenticator authenticator = new Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getDefaultInstance(properties, authenticator);

        //MimeMessage supports html
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(title + " (Do not reply)");
        msg.setSentDate(new Date());
        msg.setContent(message, "text/html");

        Transport.send(msg);
    }
}
