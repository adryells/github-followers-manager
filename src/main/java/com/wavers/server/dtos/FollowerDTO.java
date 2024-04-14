package com.wavers.server.dtos;

import org.kohsuke.github.GHUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class FollowerDTO {
    private String login;
    private String avatarUrl;
    private String htmlUrl;
    private Long githubId;

    private FollowerDTO(Long githubId, String login, String avatarUrl, String htmlUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.htmlUrl = htmlUrl;
        this.githubId = githubId;
    }


    public static Optional<FollowerDTO> fromResultSet(ResultSet rs) {
        try {
            String login = rs.getString("login");
            String avatarUrl = rs.getString("avatarUrl");
            String htmlUrl = rs.getString("htmlUrl");
            long githubId = rs.getLong("githubId");
            return Optional.of(new FollowerDTO(githubId, login, avatarUrl, htmlUrl));
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static FollowerDTO fromGHUser (GHUser ghUser) {
        return new FollowerDTO(
            ghUser.getId(),
            ghUser.getLogin(),
            ghUser.getAvatarUrl(),
            ghUser.getHtmlUrl().toString()
        );
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

