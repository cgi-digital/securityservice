package security.service.email;

import org.apache.commons.mail.MultiPartEmail;

import java.nio.file.Path;

public interface EmailProducer {

    /**
     * Function produceEmail
     *
     * This function creates or builds the email object in Multipart format so that the attachment can be added
     *
     * @param path the path where the stored file is located to be added as an attachment
     * @param attachmentName The string name to use as the name for the attachment
     * @return MultiPartEmail the multipart email object
     */
    MultiPartEmail produceEmail(Path path, String attachmentName);

    /**
     * Function sendEmail
     *
     * This function sends the email to the recipients listed in the email addresses
     * @param email The email to send
     * @param emailAddresses The array representing the list of email addresses
     */
    void sendEmail(MultiPartEmail email, String[] emailAddresses);
}
