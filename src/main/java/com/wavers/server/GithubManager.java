package com.wavers.server;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GithubManager {

    private final GitHub github;
    private final FollowerManager fm = new FollowerManager();


    public GithubManager(String ACCESS_TOKEN) throws IOException {
        this.github = new GitHubBuilder().withOAuthToken(ACCESS_TOKEN).build();
    }

    public UserDTO convertToUserData(GHUser ghUser) {
        return new UserDTO(ghUser.getId(), ghUser.getLogin(), ghUser.getAvatarUrl(), ghUser.getHtmlUrl().toString());
    }


    public List<UserDTO> getNewFollowers(String username) throws IOException {
        GHUser user = github.getUser(username);

        List<Long> followersInDb = fm.getAllUsers().stream().map(UserDTO::getGithubId).toList();

        List<UserDTO> currentFollowers = user.getFollowers().stream().map(this::convertToUserData).toList();

        return currentFollowers.stream()
                .filter(follower -> !followersInDb.contains(follower.getGithubId()))
                .peek(fm::insertUserData)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFollowers(String username) throws IOException {
        GHUser user = github.getUser(username);

        return user.getFollowers().stream().map(this::convertToUserData).toList();
    }

    public List<UserDTO> getUnfollowers(String username) throws IOException {
        GHUser user = github.getUser(username);

        List<UserDTO> followersInDb = fm.getAllUsers().stream().toList();

        List<Long> currentFollowers = user.getFollowers().stream().map(this::convertToUserData).map(UserDTO::getGithubId).toList();

        return followersInDb.stream()
                .filter(follower -> !currentFollowers.contains(follower.getGithubId()))
                .peek(fm::deleteUserData)
                .collect(Collectors.toList());
    }
}
