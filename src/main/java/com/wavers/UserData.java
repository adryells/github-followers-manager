package com.wavers;

public class UserData {
    private String login;
    private String avatarUrl;
    private String htmlUrl;

    public UserData(String login, String avatarUrl, String htmlUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.htmlUrl = htmlUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}

