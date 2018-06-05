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

/**
 * https://api.slack.com/docs/message-attachments
 */
public class SlackAttachmentDto {

    @JsonProperty("fallback")
    private String fallback;
    @JsonProperty("color")
    private String color;
    @JsonProperty("pretext")
    private String pretext;
    @JsonProperty("author_name")
    private String authorName;
    @JsonProperty("author_link")
    private String authorLink;
    @JsonProperty("author_icon")
    private String authorIcon;
    @JsonProperty("title")
    private String title;
    @JsonProperty("title_link")
    private String titleLink;
    @JsonProperty("text")
    private String text;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("thumb_url")
    private String thumbUrl;
    @JsonProperty("footer")
    private String footer;
    @JsonProperty("footer_icon")
    private String footerIcon;
    @JsonProperty("ts")
    private int timestamp;
    @JsonProperty("fields")
    private List<FieldDto> fields = new ArrayList<>();
    @JsonProperty("mrkdwn_in")
    private List<String> markdown = new ArrayList<>();

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPretext() {
        return pretext;
    }

    public void setPretext(String pretext) {
        this.pretext = pretext;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorLink() {
        return authorLink;
    }

    public void setAuthorLink(String authorLink) {
        this.authorLink = authorLink;
    }

    public String getAuthorIcon() {
        return authorIcon;
    }

    public void setAuthorIcon(String authorIcon) {
        this.authorIcon = authorIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleLink() {
        return titleLink;
    }

    public void setTitleLink(String titleLink) {
        this.titleLink = titleLink;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getFooterIcon() {
        return footerIcon;
    }

    public void setFooterIcon(String footerIcon) {
        this.footerIcon = footerIcon;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<FieldDto> getFields() {
        return fields;
    }

    public void setFields(List<FieldDto> fields) {
        this.fields = fields;
    }

    public List<String> getMarkdown() {
        return markdown;
    }

    public void setMarkdown(List<String> markdown) {
        this.markdown = markdown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlackAttachmentDto that = (SlackAttachmentDto) o;

        if (timestamp != that.timestamp) return false;
        if (fallback != null ? !fallback.equals(that.fallback) : that.fallback != null) return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        if (pretext != null ? !pretext.equals(that.pretext) : that.pretext != null) return false;
        if (authorName != null ? !authorName.equals(that.authorName) : that.authorName != null) return false;
        if (authorLink != null ? !authorLink.equals(that.authorLink) : that.authorLink != null) return false;
        if (authorIcon != null ? !authorIcon.equals(that.authorIcon) : that.authorIcon != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (titleLink != null ? !titleLink.equals(that.titleLink) : that.titleLink != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null) return false;
        if (thumbUrl != null ? !thumbUrl.equals(that.thumbUrl) : that.thumbUrl != null) return false;
        if (footer != null ? !footer.equals(that.footer) : that.footer != null) return false;
        if (footerIcon != null ? !footerIcon.equals(that.footerIcon) : that.footerIcon != null) return false;
        if (fields != null ? !fields.equals(that.fields) : that.fields != null) return false;
        return markdown != null ? markdown.equals(that.markdown) : that.markdown == null;
    }

    @Override
    public int hashCode() {
        int result = fallback != null ? fallback.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (pretext != null ? pretext.hashCode() : 0);
        result = 31 * result + (authorName != null ? authorName.hashCode() : 0);
        result = 31 * result + (authorLink != null ? authorLink.hashCode() : 0);
        result = 31 * result + (authorIcon != null ? authorIcon.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (titleLink != null ? titleLink.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (thumbUrl != null ? thumbUrl.hashCode() : 0);
        result = 31 * result + (footer != null ? footer.hashCode() : 0);
        result = 31 * result + (footerIcon != null ? footerIcon.hashCode() : 0);
        result = 31 * result + timestamp;
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        result = 31 * result + (markdown != null ? markdown.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SlackAttachmentDto{" +
                "fallback='" + fallback + '\'' +
                ", color='" + color + '\'' +
                ", pretext='" + pretext + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorLink='" + authorLink + '\'' +
                ", authorIcon='" + authorIcon + '\'' +
                ", title='" + title + '\'' +
                ", titleLink='" + titleLink + '\'' +
                ", text='" + text + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", footer='" + footer + '\'' +
                ", footerIcon='" + footerIcon + '\'' +
                ", timestamp=" + timestamp +
                ", fields=" + fields +
                ", markdown=" + markdown +
                '}';
    }

    public static class FieldDto {
        @JsonProperty("title")
        private String title;
        @JsonProperty("value")
        private String value;
        @JsonProperty("short")
        private boolean isShort;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isShort() {
            return isShort;
        }

        public void setShort(boolean isShort) {
            this.isShort = isShort;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FieldDto fieldDto = (FieldDto) o;

            if (isShort != fieldDto.isShort) return false;
            if (title != null ? !title.equals(fieldDto.title) : fieldDto.title != null) return false;
            return value != null ? value.equals(fieldDto.value) : fieldDto.value == null;
        }

        @Override
        public int hashCode() {
            int result = title != null ? title.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            result = 31 * result + (isShort ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "FieldDto{" +
                    "title='" + title + '\'' +
                    ", value='" + value + '\'' +
                    ", isShort=" + isShort +
                    '}';
        }
    }
}
