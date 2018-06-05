/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package co.kuali.coeus.sys.impl.logging.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SlackPayloadDto {

    @JsonProperty("userName")
    private String userName;
    @JsonProperty("channel")
    private String channel;
    @JsonProperty("icon_emoji")
    private String iconEmoji;
    @JsonProperty("parse")
    private String parse;
    @JsonProperty("text")
    private String text;
    @JsonProperty("attachments")
    private List<SlackAttachmentDto> attachments = new ArrayList<>();

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

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SlackAttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SlackAttachmentDto> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlackPayloadDto that = (SlackPayloadDto) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        if (iconEmoji != null ? !iconEmoji.equals(that.iconEmoji) : that.iconEmoji != null) return false;
        if (parse != null ? !parse.equals(that.parse) : that.parse != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        return attachments != null ? attachments.equals(that.attachments) : that.attachments == null;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        result = 31 * result + (iconEmoji != null ? iconEmoji.hashCode() : 0);
        result = 31 * result + (parse != null ? parse.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SlackPayloadDto{" +
                "userName='" + userName + '\'' +
                ", channel='" + channel + '\'' +
                ", iconEmoji='" + iconEmoji + '\'' +
                ", parse='" + parse + '\'' +
                ", text='" + text + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}