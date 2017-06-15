package com.alorma.universidad;

import java.util.HashMap;

/**
 * Created by a557114 on 04/06/2014.
 */
public class VersionResponse {

    private HashMap<String, Boolean> versions;
    private String url;
    private HashMap<String, String> messages;

    public HashMap<String, Boolean> getVersions() {
        return versions;
    }

    public void setVersions(HashMap<String, Boolean> versions) {
        this.versions = versions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, String> messages) {
        this.messages = messages;
    }

    public String getMessage(String code) {
        if (messages != null && code != null) {
            return messages.get(code);
        }
        return null;
    }

    public boolean isVersionEnabled(String version) {
        if (versions != null && version != null) {
            return versions.get(version);
        }
        return false;
    }
}
