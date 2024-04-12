package com.wavers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class GithubManagerApp extends JFrame {

    private final JComboBox<String> usernameComboBox;
    private final JTextField accessTokenField;
    private final JTable followersTable;
    private final SQLiteDBManager dbManager;

    public GithubManagerApp() {
        setTitle("GitHub Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dbManager = new SQLiteDBManager();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());

        usernameComboBox = new JComboBox<>();
        usernameComboBox.setEditable(true);
        List<String> usernames = dbManager.getAllUsernames();
        for (String username : usernames) {
            usernameComboBox.addItem(username);
        }
        usernameComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUsername = (String) usernameComboBox.getSelectedItem();
                String accessToken = dbManager.getAccessToken(selectedUsername);
                accessTokenField.setText(accessToken);
            }
        });
        inputPanel.add(usernameComboBox);

        accessTokenField = new JTextField(15);
        accessTokenField.setBorder(BorderFactory.createTitledBorder("Personal Access Token"));
        inputPanel.add(accessTokenField);

        JButton listFollowersButton = new JButton("All Followers");
        JButton listNewFollowersButton = new JButton("New Followers");
        JButton listUnFollowersButton = new JButton("UnFollowers");
        String username = (String) usernameComboBox.getSelectedItem();

        listFollowersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listUsers(getManager().getFollowers(username));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        listNewFollowersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listUsers(getManager().getNewFollowers(username));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        listUnFollowersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listUsers(getManager().getUnfollowers(username));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        inputPanel.add(listFollowersButton);
        inputPanel.add(listNewFollowersButton);
        inputPanel.add(listUnFollowersButton);

        JButton insertButton = new JButton("Save login");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertLogin();
            }
        });
        inputPanel.add(insertButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        followersTable = new JTable();
        followersTable.setRowHeight(50);
        JScrollPane scrollPane = new JScrollPane(followersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    private GithubManager getManager() throws IOException{
        String accessToken = accessTokenField.getText();

        return new GithubManager(accessToken);
    }

    private void listUsers(List<UserData> users) {
        try {
            DefaultTableModel model = getDefaultTableModel(users);

            followersTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = followersTable.rowAtPoint(evt.getPoint());
                    int column = followersTable.columnAtPoint(evt.getPoint());
                    if (row >= 0 && column == 0) {
                        try {
                            Desktop.getDesktop().browse(new URL(users.get(row).getHtmlUrl()).toURI());
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

            sorter.setComparator(1, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return Long.compare(o1, o2);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DefaultTableModel getDefaultTableModel(List<UserData> newFollowers) throws MalformedURLException {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Avatar");
        model.addColumn("Github ID");
        model.addColumn("Login");

        for (UserData follower : newFollowers) {
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
        dbManager.insertLogin(username, accessToken);
        usernameComboBox.addItem(username);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GithubManagerApp();
            }
        });
    }
}
