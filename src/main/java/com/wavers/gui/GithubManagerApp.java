package com.wavers.gui;

import com.wavers.server.services.GithubService;
import com.wavers.server.db.queries.LoginManager;
import com.wavers.server.dtos.FollowerDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class GithubManagerApp extends JFrame {

    private JComboBox<String> usernameComboBox;
    private JTextField accessTokenField;
    private final JTable followersTable;
    private final LoginManager loginManager = new LoginManager();

    public GithubManagerApp() {
        setTitle("GitHub Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        accessTokenField = new JTextField(15);
        accessTokenField.setBorder(BorderFactory.createTitledBorder("Personal Access Token"));

        usernameComboBox = new JComboBox<>();
        usernameComboBox.setEditable(true);

        loginManager
            .getAllLoggedUsers()
            .forEach(usernameComboBox::addItem);

        usernameComboBox.addActionListener(e -> {
            String selectedUsername = (String) usernameComboBox.getSelectedItem();
            String accessToken = loginManager.getAccessToken(selectedUsername);
            accessTokenField.setText(accessToken);
        });

        JPanel optionsPanel = getOptionsPanel();

        JButton insertButton = new JButton("Save login");
        insertButton.addActionListener(e -> insertLogin());

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(usernameComboBox);
        inputPanel.add(accessTokenField);
        inputPanel.add(insertButton);

        followersTable = new JTable();
        followersTable.setRowHeight(50);
        JScrollPane scrollPane = new JScrollPane(followersTable);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(inputPanel);
        mainPanel.add(optionsPanel);
        mainPanel.add(scrollPane);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel getOptionsPanel() {
        JButton listFollowersButton = new JButton("All Followers");
        listFollowersButton.addActionListener(e -> {
            try {
                listUsers(getManager().getFollowers());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton listNewFollowersButton = new JButton("New Followers");
        listNewFollowersButton.addActionListener(e -> {
            try {
                listUsers(getManager().getNewFollowers());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton listUnFollowersButton = new JButton("UnFollowers");
        listUnFollowersButton.addActionListener(e -> {
            try {
                listUsers(getManager().getUnfollowers());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JPanel optionsPanel = new JPanel(new FlowLayout());
        optionsPanel.add(listFollowersButton);
        optionsPanel.add(listNewFollowersButton);
        optionsPanel.add(listUnFollowersButton);
        return optionsPanel;
    }

    private GithubService getManager() throws IOException{
        String accessToken = accessTokenField.getText();
        String username = (String) usernameComboBox.getSelectedItem();
        return new GithubService(accessToken, username);
    }

    private void listUsers(List<FollowerDTO> users) {
        try {
            DefaultTableModel model = getDefaultTableModel(users);

            followersTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int viewRow = followersTable.rowAtPoint(evt.getPoint());
                    int modelRow = followersTable.convertRowIndexToModel(viewRow);
                    if (modelRow >= 0 && modelRow < users.size()) {
                        try {
                            Desktop
                                .getDesktop()
                                .browse(new URL(users.get(modelRow).getHtmlUrl()).toURI());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            followersTable.setModel(model);

            followersTable.getColumnModel().getColumn(0).setCellRenderer(new AvatarRenderer());

            followersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            followersTable.setRowSorter(sorter);

            sorter.setComparator(1, Comparator.comparingLong((Long o) -> o));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DefaultTableModel getDefaultTableModel(List<FollowerDTO> newFollowers) throws MalformedURLException {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Avatar");
        model.addColumn("Github ID");
        model.addColumn("Login");

        for (FollowerDTO follower : newFollowers) {
            ImageIcon icon = new ImageIcon(new URL(follower.getAvatarUrl()));
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImage);

            Object[] row = {icon, follower.getGithubId(), follower.getLogin()};
            model.addRow(row);
        }
        return model;
    }

    private class AvatarRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            if (value instanceof ImageIcon) {
                label.setIcon((ImageIcon) value);
            }
            return label;
        }
    }

    private void insertLogin() {
        String username = (String) usernameComboBox.getSelectedItem();
        String accessToken = accessTokenField.getText();
        loginManager.insertLogin(username, accessToken);
        usernameComboBox.addItem(username);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GithubManagerApp::new);
    }
}
