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

    public GitHubProfileInfoGUI() {
        setTitle("GitHub Profile Info");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton followersButton = new JButton("List Followers");
        JButton followingButton = new JButton("List Following");
        resultTextArea = new JTextArea();
        usernameTextField = new JTextField(20);

        followersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                listFollowers(username);
            }
        });

        followingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                listFollowing(username);
            }
        });

        JPanel mainPanel = new JPanel(new GridLayout(2, 1));

        JPanel userInputPanel = new JPanel();
        userInputPanel.add(new JLabel("GitHub Username:"));
        userInputPanel.add(usernameTextField);

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

    private void listFollowers(String username) {
        List<String> followers = getFollowers(username);

        resultTextArea.setText("Followers of " + username + ":\n");
        for (String follower : followers) {
            resultTextArea.append(follower + "\n");
        }
    }

    private void listFollowing(String username) {
        List<String> following = getFollowing(username);

        resultTextArea.setText("Following from " + username + ":\n");
        for (String followed : following) {
            resultTextArea.append(followed + "\n");
        }
    }

    private List<String> getFollowers(String username) {
        List<String> followers = new ArrayList<>();
        try {
            String url = "https://api.github.com/users/" + username + "/followers?per_page=25&page=1";
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

    private List<String> getFollowing(String username) {
        List<String> following = new ArrayList<>();
        try {
            String url = "https://api.github.com/users/" + username + "/following?per_page=25&page=1";
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

    private Integer getFollowingCount(String username) {
        Integer followingCount = 0;

        try {
            String url = "https://api.github.com/users/" + username;
            String jsonResponse = HttpClient.sendGetRequest(url);
            JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return followingCount;
    }

    private Integer getFollowersCount(String username) {
        Integer followersCount = 0;
        try {
            String url = "https://api.github.com/users/" + username;
            String jsonResponse = HttpClient.sendGetRequest(url);
            JsonArray jsonArray = JsonParser.parseString(jsonResponse).getAsJsonArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return followersCount;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GitHubProfileInfoGUI().setVisible(true);
            }
        });
    }
}
