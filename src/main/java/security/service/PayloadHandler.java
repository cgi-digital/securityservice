package security.service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.api.webhook.WebhookResponse;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Class PayloadHandler
 *
 * This class builds the payload to respond to the incoming request with a message giving feedback to the user
 *
 */
@Slf4j
public class PayloadHandler {

    /**
     * Function sendPayload
     *
     * This function builds and sends the payload back to the slack channel informing the user that something is happening
     *
     * @param slack The slack object to use
     * @param text The text as a string representing the text to send in the payload body
     * @param channelId The string representing the channel id to respond to
     * @param responseUrl The string representing the response url to send the response to
     */
    public static void sendPayload(Slack slack, String text, String channelId, String responseUrl) {
        val payload = Payload.builder().channel(channelId).text(text).username("CGI security audit").build();
        try {
            WebhookResponse response = slack.send(responseUrl, payload);
            log.info("Webhook response from security audit request {}", response.getBody());
        } catch (IOException e) {
            log.error("Exception sending response to webhook url {}", e.getMessage());
        }
    }
}
