package security.service;

import com.github.seratch.jslack.Slack;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import security.service.audit.SlackHistoryAuditor;

import java.util.Arrays;

import static io.micronaut.http.HttpStatus.OK;

/**
 * Class SecurityController
 *
 * This class is the main entry point for any request to the service.
 *
 * It holds 1 function that handles the incoming request to audit the channels
 */
@Slf4j
@Controller("/security")
public class SecurityController {

    private Slack slack;
    private String token;

    public SecurityController() {
        this.slack = new Slack();
        this.token = System.getenv("AUTH_TOKEN");
    }

    /**
     * Function doAudit
     *
     * This is the only function within the controller and handles the incoming request to start the audit process
     * for channels within the slack workspace
     *
     * The function takes the incoming request and processes that to determine if the user request is listed as an admin
     * user, rejecting the request if the user is not an admin
     *
     * If the user is an admin user a separate thread is started to run the audit of the channels and the function returns
     * an HttpResponse to the slack server. ** Warning ** The response is time limited to 3000ms if the response falls outside
     * of this time limit the request is cued to retry exponentially until a limit of 5 minutes after that time the request is cancelled
     *
     * The thread runs outside of the request/response cycle because it can never be known how long it will take to create the files
     * as such this has to run as a separate process or co-routine to the main thread which needs to respond within the 3000ms timeout limit
     *
     * @param channelId The string representing the channel id
     * @param responseUrl The string representing the response url
     * @param text The string representing the text as a parameter to the incoming request
     * @param userId The string representing the user id
     * @param username The string representing the username
     * @return HttpResponse the HttpResponse object
     */
    @Post(value = "/audit", consumes = {MediaType.APPLICATION_JSON, MediaType.ALL, MediaType.APPLICATION_FORM_URLENCODED})
    public HttpResponse doAudit(@QueryValue("channel_id") String channelId,
                                @QueryValue("response_url") String responseUrl,
                                @QueryValue("text") String text,
                                @QueryValue("user_id") String userId,
                                @QueryValue("user_name") String username) {
        if (!isUserAdmin(username)) {
            val message = "You are not listed as administrator for slack, you cannot access this command";
            PayloadHandler.sendPayload(slack, message, channelId, responseUrl);
            return HttpResponse.status(OK);
        }

        Thread threadable = new Thread(new SlackHistoryAuditor(slack, channelId, responseUrl, text));
        threadable.start();

        String messageResponseText = "We'll contact you with a slack message and an email to let you know when the file is ready to view with it's location";
        PayloadHandler.sendPayload(slack, messageResponseText, channelId, responseUrl);

        return HttpResponse.status(OK);
    }

    private boolean isUserAdmin(String username) {
        if (System.getenv("ADMIN_USERS") != null) {
            val adminName = Arrays.stream(
                    System.getenv("ADMIN_USERS").split(",")
            ).filter(admin -> admin.equals(username)).findFirst().orElse(null);
            return adminName != null;
        }
        return false;
    }
}