/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package co.kuali.coeus.sys.impl.logging.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.kuali.kra.infrastructure.Constants;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class SlackAppender extends AppenderSkeleton {

    private String url;
    private String userName;
    private String channel;
    private String iconEmoji;

    @Override
    protected void append(LoggingEvent event) {

        if (StringUtils.isNotBlank(url)) {
            final HttpClient instance = HttpClientBuilder.create().build();
            final HttpPost post = new HttpPost(url);
            post.setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            final ObjectMapper objectMapper = new ObjectMapper();

            final SlackPayloadDto payloadDto = createPayloadDto(event);
            try {
                post.setEntity(new StringEntity(objectMapper.writeValueAsString(payloadDto)));
                logResponse(instance.execute(post), payloadDto);
            } catch (IOException e) {
                LogLog.error("Unable to to send serialized payload " + payloadDto + " to url " + url);
            }
        } else {
            LogLog.error(SlackAppender.class.getName() + " is not configured with a url");
        }
    }

    private void logResponse(HttpResponse response, SlackPayloadDto payloadDto) throws IOException {
        final int responseStatus = response.getStatusLine().getStatusCode();
        final String resposeString = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        final String responseMessage = "Response received. Payload " + payloadDto + " url " + url + " response status " + responseStatus + " response " + resposeString;
        if (responseStatus < 400) {
            LogLog.debug(responseMessage);
        } else {
            LogLog.error(responseMessage);
        }
    }

    private SlackPayloadDto createPayloadDto(LoggingEvent event) {
        final SlackPayloadDto payloadDto = new SlackPayloadDto();

        if (StringUtils.isNotBlank(userName)) {
            payloadDto.setUserName(userName);
        }

        if (StringUtils.isNotBlank(channel)) {
            payloadDto.setChannel(channel);
        }

        if (StringUtils.isNotBlank(iconEmoji)) {
            payloadDto.setIconEmoji(iconEmoji);
        }

        final SlackAttachmentDto attachmentDto = new SlackAttachmentDto();
        attachmentDto.setColor(Integer.toHexString(getColor(event).getRGB()).substring(2));
        attachmentDto.setPretext(escapeSlackString(event.getMessage().toString()));
        attachmentDto.setTitle(escapeSlackString(event.getLevel().toString()));

        final String fallback = createFallback(event);
        attachmentDto.setFallback(escapeSlackString(fallback));

        final String stacktrace = createThrowableString(event);
        if (StringUtils.isNotBlank(stacktrace)) {
            attachmentDto.setText(escapeSlackString(fallback + "\n" + stacktrace));
        } else {
            attachmentDto.setText(escapeSlackString(fallback));
        }

        payloadDto.setAttachments(Collections.singletonList(attachmentDto));

        return payloadDto;
    }

    private String createFallback(LoggingEvent event) {
        final StringBuilder fallback = new StringBuilder();
        fallback.append(event.getLevel().toString());
        fallback.append(" ");
        fallback.append(event.getMessage().toString());
        return fallback.toString();
    }

    private String createThrowableString(LoggingEvent event) {
        if (layout != null && !layout.ignoresThrowable() && event.getThrowableInformation() != null && event.getThrowableInformation().getThrowable() != null) {
            final StringWriter stackTrace = new StringWriter();
            try (PrintWriter writer = new PrintWriter(stackTrace)) {
                event.getThrowableInformation().getThrowable().printStackTrace(writer);
                return stackTrace.toString();
            }
        }

        return null;
    }

    private Color getColor(LoggingEvent event) {
        switch (event.getLevel().toInt()) {
            case Level.FATAL_INT:
            case Level.ERROR_INT:
                return Color.RED;
            case Level.WARN_INT:
                return Color.ORANGE;
            case Level.INFO_INT:
                return Color.BLACK;
            case Level.DEBUG_INT:
                return Color.BLUE;
            case Level.TRACE_INT:
                return Color.GREEN;
            default:
                return Color.GRAY;
        }
    }

    @Override
    public void close() {
        //no op
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getIconEmoji() {
        return iconEmoji;
    }

    public void setIconEmoji(String iconEmoji) {
        this.iconEmoji = iconEmoji;
    }

    /**
     * https://api.slack.com/docs/message-formatting
     *
     * Replace the ampersand, &, with &amp;
     * Replace the less-than sign, < with &lt;
     * Replace the greater-than sign, > with &gt;
     */
    private static String escapeSlackString(String text) {
        if (text == null) {
            return null;
        }

        return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
