package com.wavers;

public class UserData {
    private String login;
    private String avatarUrl;
    private String htmlUrl;
    private Long githubId;

    public UserData(Long githubId, String login, String avatarUrl, String htmlUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.htmlUrl = htmlUrl;
        this.githubId = githubId;
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

    public Long getGithubId() {
        return githubId;
    }

    public void setGithubId(Long githubId) {
        this.githubId = githubId;
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

