package com.wavers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class GithubManagerApp extends JFrame {

    private final JTextField usernameField;
    private final JTextField accessTokenField;
    private final JLabel newFollowersLabel;
    private final JLabel unfollowersLabel;

    public GithubManagerApp() {
        setTitle("GitHub Manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createTitledBorder("GitHub Username"));
        panel.add(usernameField);

        accessTokenField = new JTextField();
        accessTokenField.setBorder(BorderFactory.createTitledBorder("Personal Access Token"));
        panel.add(accessTokenField);

        JButton listFollowersButton = new JButton("List Followers");
        listFollowersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listFollowers();
            }
        });
        panel.add(listFollowersButton);

        newFollowersLabel = new JLabel();
        panel.add(newFollowersLabel);

        unfollowersLabel = new JLabel();
        panel.add(unfollowersLabel);

        add(panel);
        setVisible(true);
    }

    private void listFollowers() {
        try {
            String username = usernameField.getText();
            String accessToken = accessTokenField.getText();

            GithubManager githubManager = new GithubManager(accessToken);

            List<UserData> newFollowers = githubManager.getNewFollowers(username);
            List<UserData> unfollowers = githubManager.getUnfollowers(username);

            newFollowersLabel.setText("New Followers: " + newFollowers.size());
            unfollowersLabel.setText("Unfollowers: " + unfollowers.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GithubManagerApp();
            }
        });
    }
}
