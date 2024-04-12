package com.wavers;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GithubManager {

    private final GitHub github;
    private final SQLiteDBManager dbManager = new SQLiteDBManager();


    public GithubManager(String ACCESS_TOKEN) throws IOException {
        this.github = new GitHubBuilder().withOAuthToken(ACCESS_TOKEN).build();
    }

    public UserData convertToUserData(GHUser ghUser) {
        return new UserData(ghUser.getId(), ghUser.getLogin(), ghUser.getAvatarUrl(), ghUser.getHtmlUrl().toString());
    }


    public List<UserData> getNewFollowers(String username) throws IOException {
        GHUser user = github.getUser(username);

        List<Long> followersInDb = dbManager.getAllUsers().stream().map(UserData::getGithubId).toList();

        List<UserData> currentFollowers = user.getFollowers().stream().map(this::convertToUserData).toList();

        return currentFollowers.stream()
                .filter(follower -> !followersInDb.contains(follower.getGithubId()))
                .peek(dbManager::insertUserData)
                .collect(Collectors.toList());
    }

    public List<UserData> getFollowers(String username) throws IOException {
        GHUser user = github.getUser(username);

        return user.getFollowers().stream().map(this::convertToUserData).toList();
    }

    public List<UserData> getUnfollowers(String username) throws IOException {
        GHUser user = github.getUser(username);

        List<UserData> followersInDb = dbManager.getAllUsers().stream().toList();

        List<Long> currentFollowers = user.getFollowers().stream().map(this::convertToUserData).map(UserData::getGithubId).toList();

        return followersInDb.stream()
                .filter(follower -> !currentFollowers.contains(follower.getGithubId()))
                .collect(Collectors.toList());
    }
}
