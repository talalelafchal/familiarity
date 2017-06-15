package com.example.jenny.myapplication.data;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Objects;

/**
 * Data structure for photo.
 */
public class Photo implements Serializable {
    private String id;
    private String thumbUrl;
    private String url;
    private String title;
    private String farmId;
    private String serverId;
    private String secret;

    public Photo(String id, String thumbUrl, String url, String title) {
        this.id = id;
        this.thumbUrl = thumbUrl;
        this.url = url;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id)
                + Objects.hashCode(title) * 7
                + Objects.hashCode(url) * 31
                + Objects.hashCode(thumbUrl) * 37;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Photo)) {
            return false;
        }
        return Objects.equals(title, ((Photo) obj).title)
                && Objects.equals(url, ((Photo) obj).url)
                && Objects.equals(url, ((Photo) obj).thumbUrl)
                && Objects.equals(id, ((Photo) obj).id);
    }
}
