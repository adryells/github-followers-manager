package com.wavers.server.services;

import com.wavers.server.db.queries.FollowerManager;
import com.wavers.server.dtos.FollowerDTO;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GithubService {

    private final GitHub github;
    private final GHUser currentUser;
    private final FollowerManager followerManager = new FollowerManager();


    public GithubService(String ACCESS_TOKEN, String username) throws IOException {
        this.github = new GitHubBuilder().withOAuthToken(ACCESS_TOKEN).build();
        this.currentUser = github.getUser(username);
    }

    public List<FollowerDTO> getNewFollowers() throws IOException {
        List<Long> followersInDb = followerManager
            .getAllUsers()
            .stream()
            .map(FollowerDTO::getGithubId)
            .toList();

        List<FollowerDTO> currentFollowers = currentUser
            .getFollowers()
            .stream()
            .map(FollowerDTO::fromGHUser)
            .toList();

        return currentFollowers
            .stream()
            .filter(follower -> !followersInDb.contains(follower.getGithubId()))
            .peek(followerManager::insertFollowerData)
            .collect(Collectors.toList());
    }

    public List<FollowerDTO> getFollowers() throws IOException {
        return currentUser
            .getFollowers()
            .stream()
            .map(FollowerDTO::fromGHUser)
            .toList();
    }

    public List<FollowerDTO> getUnfollowers() throws IOException {
        List<FollowerDTO> followersInDb = followerManager.getAllUsers();

        List<Long> currentFollowers = currentUser
            .getFollowers()
            .stream()
            .map(FollowerDTO::fromGHUser)
            .map(FollowerDTO::getGithubId)
            .toList();

        return followersInDb
            .stream()
            .filter(
                follower -> !currentFollowers.contains(follower.getGithubId())
            )
            .peek(followerManager::deleteFollowerData)
            .collect(Collectors.toList());
    }
}
