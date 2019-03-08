package security.service.audit;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.File;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import security.service.email.EmailProducer;
import security.service.email.impl.EmailProducerImpl;
import security.service.html.HtmlProducer;
import security.service.html.impl.HtmlProducerImpl;

import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

/**
 * Class SlackSecurityJob
 *
 * This class is a scheduled job class that runs on the first day of the month every month
 *
 * The scheduled job does not handle incoming request information, it is a blunt instrument to scrape the files
 * from all slack channels and produce an audit output.
 *
 *
 */
@Slf4j
@Singleton
public class SlackSecurityJob extends Audit {
    private Path path;
    private HtmlProducer htmlProducer;
    private EmailProducer emailProducer;

    public SlackSecurityJob() {
        this.setSlack(new Slack());
        this.setToken(System.getenv("AUTH_TOKEN"));
        this.htmlProducer = new HtmlProducerImpl();
        this.emailProducer = new EmailProducerImpl();
    }

    /**
     * Function process
     *
     * This function will run between Midnight and 01:00 on the first day of the month every month
     *
     * The function scrapes the slack channels within the workspace tied to the token and produces the output HTML file
     *
     * It sends an email to the listed email addresses that the file has been produced and the file is attached to that email
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void process() {
        val builder = new StringBuilder();

        this.setupChannelsAndUsers();

        try {

            val response = this.getFilesFromRequest();

            for (File file : response) {
                String html = htmlProducer.constructHtmlString(file, this.getChannelsListResponse(), this.getUsersListResponse());
                builder.append(html);
            }

            val finalHtmlString = htmlProducer.constructHtmlDocument(null, builder);

            path = buildJobPath();
            val fileExists = Files.exists(path);

            if (!fileExists) {
                log.info("File does not exist, will create the new audit file");
                Files.createFile(path);
                Files.write(path, finalHtmlString.getBytes());
            } else {
                log.info("File already exists, will append to the existing file");
                Files.write(path, finalHtmlString.getBytes(), StandardOpenOption.APPEND);
            }

            val emailAddresses = System.getenv("EMAIL_ADDR").split(",");
            val email = emailProducer.produceEmail(path, "Scheduled audit");

            emailProducer.sendEmail(email, emailAddresses);

        } catch (SlackApiException | IOException ex) {
            log.error("Exception within slack library: {}", ex.getMessage());
        }
        log.info("Audit file has been created, check your directories at the following path {}", path);
    }

    private Path buildJobPath() {
        return Paths.get(System.getenv("FILE_PATH") + "/audit_" + LocalDate.now().getMonth() + "_" +
                LocalDate.now().getYear() + "_scheduled.html");
    }
}