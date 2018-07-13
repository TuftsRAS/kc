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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.status.StatusLogger;
import org.kuali.kra.infrastructure.Constants;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Plugin(name = "Slack", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class SlackAppender extends AbstractAppender {

    private final String webhookUrl;
    private final String userName;
    private final String channel;
    private final String iconEmoji;

    public SlackAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, String webhookUrl, String userName, String channel, String iconEmoji) {
        super(name, filter, layout, ignoreExceptions);
        this.webhookUrl = webhookUrl;
        this.userName = userName;
        this.channel = channel;
        this.iconEmoji = iconEmoji;
    }

    @PluginBuilderFactory
    public static SlackAppenderBuilder createAppender() {
        return new SlackAppenderBuilder();
    }

    @Override
    public void append(LogEvent event) {

        if (StringUtils.isNotBlank(webhookUrl)) {
            final HttpClient instance = HttpClientBuilder.create().build();
            final HttpPost post = new HttpPost(webhookUrl);
            post.setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            final ObjectMapper objectMapper = new ObjectMapper();

            final SlackPayloadDto payloadDto = createPayloadDto(event);
            try {
                post.setEntity(new StringEntity(objectMapper.writeValueAsString(payloadDto)));
                logResponse(instance.execute(post), payloadDto);
            } catch (IOException e) {
                StatusLogger.getLogger().error("Unable to to send serialized payload " + payloadDto + " to url " + webhookUrl);
            }
        } else {
            StatusLogger.getLogger().error(SlackAppender.class.getName() + " is not configured with a url");
        }
    }

    private void logResponse(HttpResponse response, SlackPayloadDto payloadDto) throws IOException {
        final int responseStatus = response.getStatusLine().getStatusCode();
        final String resposeString = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        final String responseMessage = "Response received. Payload " + payloadDto + " url " + webhookUrl + " response status " + responseStatus + " response " + resposeString;
        if (responseStatus < 400) {
            StatusLogger.getLogger().debug(responseMessage);
        } else {
            StatusLogger.getLogger().error(responseMessage);
        }
    }

    private SlackPayloadDto createPayloadDto(LogEvent event) {
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

    private String createFallback(LogEvent event) {
        final StringBuilder fallback = new StringBuilder();
        fallback.append(event.getLevel().toString());
        fallback.append(" ");
        fallback.append(event.getMessage().toString());
        return fallback.toString();
    }

    private String createThrowableString(LogEvent event) {
        if (event.getThrownProxy() != null && !ignoreExceptions()) {
            return event.getThrownProxy().getExtendedStackTraceAsString();
        }

        return null;
    }

    private Color getColor(LogEvent event) {
        final Level level = event.getLevel();
        if (Level.FATAL.equals(level)) {
            return Color.RED;
        } else if (Level.ERROR.equals(level)) {
            return Color.RED;
        } else if (Level.WARN.equals(level)) {
            return Color.ORANGE;
        } else if (Level.INFO.equals(level)) {
            return Color.BLACK;
        } else if (Level.DEBUG.equals(level)) {
            return Color.BLUE;
        } else if (Level.TRACE.equals(level)) {
            return Color.GREEN;
        } else {
            return Color.GRAY;
        }
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

    public static class SlackAppenderBuilder<B extends Builder<B>> extends AbstractAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<SlackAppender> {

        @PluginBuilderAttribute
        @Required(message = "Slack Webhook URL required")
        private String webhookUrl;

        @PluginBuilderAttribute
        @Required(message = "Slack channel required")
        private String channel;

        @PluginBuilderAttribute
        @Required(message = "Slack username/bot-name required")
        private String username;

        @PluginBuilderAttribute
        private String iconEmoji;

        public SlackAppenderBuilder withWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            return this;
        }

        public SlackAppenderBuilder withChannel(String channel) {
            this.channel = channel;
            return this;
        }

        public SlackAppenderBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public SlackAppenderBuilder withIconEmoji(String iconEmoji) {
            this.iconEmoji = iconEmoji;
            return this;
        }

        @Override
        public SlackAppender build() {
            return new SlackAppender(getName(), getFilter(), getLayout(), isIgnoreExceptions(), webhookUrl, channel, username, iconEmoji);
        }
    }
}
