package security.service.email.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import security.service.email.EmailProducer;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class EmailProducerImpl implements EmailProducer
 *
 */
@Slf4j
public class EmailProducerImpl implements EmailProducer {

    /**
     * {@inheritDoc}
     * @param path the path where the stored file is located to be added as an attachment
     * @param attachmentName The string name to use as the name for the attachment
     * @return
     */
    @Override
    public MultiPartEmail produceEmail(Path path, String attachmentName) {
        log.info("Creating the email to send to admin users");

        // Create the attachment
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(path.toString());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Slack audit file");
        attachment.setName(attachmentName);

        // Create the email message
        MultiPartEmail email = new MultiPartEmail();

        try {
            email.setHostName("");//TODO
            email.setFrom("");//TODO
            email.setAuthentication("", "");//TODO
            email.setSubject("Slack audit file");
            email.setMsg("Slack audit file ready to view");

            email.attach(attachment);

        } catch (EmailException ex) {
            log.error("Email exception happened {}", ex.getMessage());
        }

     return email;
    }

    /**
     * {@inheritDoc}
     * @param email The email to send
     * @param emailAddresses The array representing the list of email addresses
     */
    @Override
    public void sendEmail(MultiPartEmail email, String[] emailAddresses) {
        List<InternetAddress> inetAddresses = new ArrayList<>();
        for (String emailAddress : emailAddresses) {
            try {
                InternetAddress address = new InternetAddress(emailAddress);
                inetAddresses.add(address);
            } catch (AddressException e) {
                log.error("Unable to create the internet address from the given email address, {}", e.getMessage());
            }
        }

        try {
            email.setTo(inetAddresses);
            email.send();
        } catch (EmailException e) {
            log.error("Unable to send the bulk email to admin users, {}", e.getMessage());
        }

    }
}
