package security.service.audit;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.Channel;
import com.github.seratch.jslack.api.model.File;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import security.service.PayloadHandler;
import security.service.email.EmailProducer;
import security.service.email.impl.EmailProducerImpl;
import security.service.html.HtmlProducer;
import security.service.html.impl.HtmlProducerImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

/**
 * Class SlackHistoryAuditor
 *
 * Class processes the incoming request to conduct an audit of slack channels to produce the output file
 *
 * This class extends the Audit class and implements the Runnable interface
 */
@Slf4j
public class SlackHistoryAuditor extends Audit implements Runnable {
    private Path path;
    private String channelId;
    private String responseUrl;
    private HtmlProducer htmlProducer;
    private EmailProducer emailProducer;
    private String parameters;

    public SlackHistoryAuditor(Slack slack, String channelId, String responseUrl, String parameters) {
        this.setSlack(slack);
        this.setToken(System.getenv("AUTH_TOKEN"));
        this.channelId = channelId;
        this.responseUrl = responseUrl;
        this.htmlProducer = new HtmlProducerImpl();
        this.emailProducer = new EmailProducerImpl();
        this.parameters = parameters;
    }

    /**
     * Function run
     *
     * This function gets the files and processes them according to any incoming request parameters and produces an HTML
     * file notifying the requester that the file has been produced and sending an email to all listed email addresses with
     * the file attached
     */
    @Override
    public void run() {
        val builder = new StringBuilder();

        this.setupChannelsAndUsers();

        try {

            val files = this.getFilesFromRequest();
            val channel = getChannel();

            if(channel != null) {
                for(File file: files) {
                    if(file.getChannels().contains(channel.getId())) {
                        String html = htmlProducer.constructHtmlString(file, this.getChannelsListResponse(), this.getUsersListResponse());
                        builder.append(html);
                    }
                }
            } else {
                for (File file : files) {
                    String html = htmlProducer.constructHtmlString(file, this.getChannelsListResponse(), this.getUsersListResponse());
                    builder.append(html);
                }
            }

            String finalHtmlString;
            if(parameters != null) {
                finalHtmlString = htmlProducer.constructHtmlDocument(parameters, builder);
            } else {
                finalHtmlString = htmlProducer.constructHtmlDocument(null, builder);
            }

            path = buildPath();
            val fileExists = Files.exists(path);

            if (!fileExists) {
                log.info("File does not exist, will create the new audit file");
                Files.createFile(path);
                Files.write(path, finalHtmlString.getBytes());
            } else {
                log.info("File already exists, will append to the existing file");
                Files.write(path, finalHtmlString.getBytes(), StandardOpenOption.APPEND);
            }

            val message = "The audit file has been created and saved to " + path;
            PayloadHandler.sendPayload(this.getSlack(), message, channelId, responseUrl);

            val emailAddresses = System.getenv("EMAIL_ADDR").split(",");
            val email = emailProducer.produceEmail(path, "Adhoc command audit");

            emailProducer.sendEmail(email, emailAddresses);

        } catch (SlackApiException | IOException ex) {
            log.error("Exception within slack library: {}", ex.getMessage());
        }
        log.info("Audit file has been created, check your directories at the following path {}", path);
    }

    private Path buildPath() {
        return Paths.get(System.getenv("FILE_PATH") + "/audit_" + LocalDate.now().getDayOfMonth() + "_" +
                LocalDate.now().getMonth() + "_" + LocalDate.now().getYear() + "_command.html");
    }

    private Channel getChannel() {
        Channel channel = null;
        if(!parameters.isEmpty()) {
            channel = this.getChannelsListResponse().getChannels()
                    .stream().filter(c -> c.getName().equals(parameters)).findFirst().orElse(null);
        }
        return channel;
    }
}
