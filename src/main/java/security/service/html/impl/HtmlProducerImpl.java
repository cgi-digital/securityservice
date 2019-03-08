package security.service.html.impl;

import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.Channel;
import com.github.seratch.jslack.api.model.File;
import com.github.seratch.jslack.api.model.User;
import lombok.val;
import security.service.html.HtmlProducer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class HtmlProducerImpl implement HtmlProducer
 */
public class HtmlProducerImpl implements HtmlProducer {

    /**
     * {@inheritDoc}
     *
     */
    public String constructHtmlString(final File file, ChannelsListResponse channelsListResponse, UsersListResponse usersListResponse) {
        val builder = new StringBuilder();
        builder.append("<tr>" +
                "<td>" + file.getTitle() + "</td>" +
                "<td>" + "<a href=\"" + file.getPermalink() + "\" target='_blank'>Document Link</a>" + "</td>");


        if (file.getPermalinkPublic() != null) {
            builder.append("<td>" + "<a href=\"" + file.getPermalinkPublic() + "\">Public Document Link</a>" + "</td>");
        } else {
            builder.append("<td></td>");
        }

        val channels = new ArrayList<String>();
        val users = new ArrayList<String>();

        for (Channel c : channelsListResponse.getChannels()) {
            for (String s : file.getChannels()) {
                if (c.getId().equals(s)) {
                    channels.add(c.getName());
                }
            }
        }

        for (User u : usersListResponse.getMembers()) {
            if (u.getId().equals(file.getUser())) {
                users.add(u.getName());
            }
        }

        Date date = new Date(file.getTimestamp().longValue() * 1000);

        builder.append("<td>" + channels + "</td>" +
                "<td>" + users + "</td>" +
                "<td>" + date + "</td>" +
                "</tr>");

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     *
     */
    public String constructHtmlDocument(String parameters, StringBuilder builder) {
        String channelTitle;
        if(parameters != null) {
            channelTitle = parameters;
        } else {
            channelTitle = "All Channels";
        }
        return "<!DOCTYPE html> " +
                "<html>" +
                "<head>" +
                "<title>Slack security " + LocalDateTime.now().getMonth() + LocalDateTime.now().getDayOfMonth() + "</title>" +
                "<body>" +
                "<h2>Security audit for slack " + channelTitle + "</h2>" +
                "<table style=\"width:100%\">" +
                "<tr>" +
                "<th>File Title</th>" +
                "<th>File Permalink</th>" +
                "<th>Public Permalink</th>" +
                "<th>Channels Uploaded To</th>" +
                "<th>Uploaded By</th>" +
                "<th>Upload Timestamp</th>" +
                "</tr>" +
                builder.toString() +
                "</table>" +
                "</body>" +
                "</html>";
    }
}
