package security.service.html;

import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.File;

public interface HtmlProducer {

    /**
     * Function constructHtmlString
     *
     * This function build the html formatted string to add to the document
     *
     * @param file The file containing the details to be put into the string
     * @param channelsListResponse The channels list response containing the information related to the channel to where the
     *                             file was uploaded
     * @param usersListResponse The user list response containing the information related to the user that uploaded the
     *                          file
     * @return String the string representing the html formatted string to be added to the document
     */
    String constructHtmlString(final File file, ChannelsListResponse channelsListResponse, UsersListResponse usersListResponse);

    /**
     * Function constructHtmlDocument
     *
     * This function constructs the formatted HTML document to be written and stored
     *
     * @param parameters The paramter(s) which makes up the title of the page
     * @param builder The builder which contains the formatted html string to be input into the document
     * @return String the formatted html document which is stored as a final .html document
     */
    String constructHtmlDocument(String parameters, StringBuilder builder);
}
