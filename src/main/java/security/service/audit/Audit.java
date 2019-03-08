package security.service.audit;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import com.github.seratch.jslack.api.methods.request.files.FilesListRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.File;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Abstract Class Audit
 *
 * This forms the class hierarchy for the slack audit classes
 *
 * Common functions and variables are held here.
 */
@Slf4j
@Data
public abstract class Audit {

    private Slack slack;
    private String token;
    private ChannelsListResponse channelsListResponse;
    private UsersListResponse usersListResponse;

    /**
     * Function setupChannelsAndUsers
     *
     * This function sets up the channels list response and the users list response for use later within the audit classers
     */
    protected void setupChannelsAndUsers() {
        try {

            channelsListResponse = slack.methods().channelsList(ChannelsListRequest.builder().token(token).build());
            usersListResponse = slack.methods().usersList(UsersListRequest.builder().token(token).build());

        } catch (IOException | SlackApiException e) {
            log.error("An exception occurred attempting to get the channels or users {}", e.getMessage());
        }
    }

    /**
     * Function getFilesFromRequest
     *
     * This function builds a FilesListRequest to get all files uploaded to all slack channels within the workspace
     * anbd returns a list of all of those files for later processing
     *
     * @return List the list object representing a list of all files that have been uploaded
     * @throws IOException
     * @throws SlackApiException
     */
    protected List<File> getFilesFromRequest() throws IOException, SlackApiException {
        return this.getSlack().methods().filesList(FilesListRequest.builder().token(this.getToken()).build()).getFiles();
    }


}
