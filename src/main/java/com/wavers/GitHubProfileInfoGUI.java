package com.wavers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitHubProfileInfoGUI extends JFrame {
    private final JTextArea resultTextArea;
    private final JTextField usernameTextField;
    private JsonElement searchedUser;
    private final JButton followersButton;
    private final JButton followingButton;
    private int followersCount;
    private int followingCount;

    public GitHubProfileInfoGUI() {
        setTitle("GitHub Profile Info");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton searchButton = new JButton("Search User");
        resultTextArea = new JTextArea();
        usernameTextField = new JTextField(20);

        followersButton = new JButton("List Followers");
        followingButton = new JButton("List Following");
        followersButton.setVisible(false);
        followingButton.setVisible(false);

        searchButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String username = usernameTextField.getText();
                        searchUser(username);
                        followersButton.setVisible(true);
                        followingButton.setVisible(true);
                    }
                }
        );

        followersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPaginationButtons("followers");
            }
        });

        followingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPaginationButtons("following");
            }
        });

        JPanel mainPanel = new JPanel(new GridLayout(2, 1));

        JPanel userInputPanel = new JPanel();
        userInputPanel.add(new JLabel("GitHub Username:"));
        userInputPanel.add(usernameTextField);
        userInputPanel.add(searchButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(followersButton);
        buttonPanel.add(followingButton);

        mainPanel.add(userInputPanel);
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void searchUser(String username) {
        try {
            String url = "https://api.github.com/users/" + username;
            String jsonResponse = HttpClient.sendGetRequest(url);
            searchedUser = JsonParser.parseString(jsonResponse);
            followersCount = searchedUser.getAsJsonObject().get("followers").getAsInt();
            followingCount = searchedUser.getAsJsonObject().get("following").getAsInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPaginationButtons(String type) {
        int totalCount = type.equals("followers") ? followersCount : followingCount;
        int totalPages = (totalCount + 24) / 25;

        JFrame paginationFrame = new JFrame(type.equals("followers") ? "Followers Pagination" : "Following Pagination");
        paginationFrame.setLayout(new FlowLayout());

        for (int i = 1; i <= totalPages; i++) {
            JButton pageButton = new JButton(Integer.toString(i));
            int page = i;
            pageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handlePaginationClick(type, page);
                }
            });
            paginationFrame.add(pageButton);
        }

        paginationFrame.pack();
        paginationFrame.setVisible(true);
    }

    private void handlePaginationClick(String type, int page) {
        String username = usernameTextField.getText();
        if (type.equals("followers")) {
            List<String> followers = getFollowersWithPagination(username, page);
            displayFollowers(followers);
        } else {
            List<String> following = getFollowingWithPagination(username, page);
            displayFollowing(following);
        }
    }

    private List<String> getFollowersWithPagination(String username, int page) {
        List<String> followers = new ArrayList<>();
        try {
            String url = "https://api.github.com/users/" + username + "/followers?per_page=25&page=" + page;
            String jsonResponse = HttpClient.sendGetRequest(url);
            JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                followers.add(jsonElement.getAsJsonObject().get("login").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return followers;
    }

    private List<String> getFollowingWithPagination(String username, int page) {
        List<String> following = new ArrayList<>();
        try {
            String url = "https://api.github.com/users/" + username + "/following?per_page=25&page=" + page;
            String jsonResponse = HttpClient.sendGetRequest(url);
            JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                following.add(jsonElement.getAsJsonObject().get("login").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return following;
    }

    private void displayFollowers(List<String> followers) {
        resultTextArea.setText("Followers:\n");
        for (String follower : followers) {
            resultTextArea.append(follower + "\n");
        }
    }

    private void displayFollowing(List<String> following) {
        resultTextArea.setText("Following:\n");
        for (String followed : following) {
            resultTextArea.append(followed + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GitHubProfileInfoGUI().setVisible(true);
            }
        });
    }
}
