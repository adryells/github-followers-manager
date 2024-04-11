package com.wavers;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GitHubFollowerNotifier {

    private static final String GITHUB_USERNAME = "adryells";
    private static final String PERSONAL_ACCESS_TOKEN = "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";

    private final GitHub github;

    public GitHubFollowerNotifier() throws IOException {
        this.github = new GitHubBuilder().withOAuthToken(PERSONAL_ACCESS_TOKEN).build();
    }

    public UserData convertToUserData(GHUser ghUser) {
        UserData userData = new UserData(ghUser.getLogin(), ghUser.getAvatarUrl(), ghUser.getHtmlUrl().toString());
        SQLiteDBManager dbManager = new SQLiteDBManager();
        dbManager.insertUserData(userData);
        return userData;
    }


    public List<String> getFollowers(String username) throws IOException {
        GHUser user = github.getUser(username);
        return user.getFollowers().stream().map(GHUser::getLogin).toList();
    }

    public void updateFollowersList() {
        try {
            List<String> oldFollowers = getFollowers(GITHUB_USERNAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        GitHubFollowerNotifier notifier;
        try {
            notifier = new GitHubFollowerNotifier();
            notifier.updateFollowersList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
