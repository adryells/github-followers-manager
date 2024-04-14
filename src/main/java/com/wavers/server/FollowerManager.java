package com.wavers.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowerManager extends QueryBase {
    public void insertUserData(UserDTO userDTO) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String checkSql = String.format("SELECT COUNT(*) FROM users WHERE githubId = %d", userDTO.getGithubId());
            ResultSet resultSet = stmt.executeQuery(checkSql);
            resultSet.next();
            int count = resultSet.getInt(1);
            resultSet.close();

            if (count == 0) {
                String insertSql = String.format(
                    "INSERT INTO users (login, avatarUrl, htmlUrl, githubId) VALUES ('%s', '%s', '%s', %d)",
                    userDTO.getLogin(), userDTO.getAvatarUrl(), userDTO.getHtmlUrl(), userDTO.getGithubId()
                );
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public List<UserDTO> getAllUsers() {
        List<UserDTO> userList = new ArrayList<>();

        try (
            Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet result = stmt.executeQuery()
        ) {
            while (result.next()) {
                String login = result.getString("login");
                String avatarUrl = result.getString("avatarUrl");
                String htmlUrl = result.getString("htmlUrl");
                long githubId = result.getLong("githubId");

                UserDTO userDTO = new UserDTO(githubId, login, avatarUrl, htmlUrl);
                userList.add(userDTO);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return userList;
    }

    public void deleteUserData(UserDTO userDTO) {
        try (
            Connection conn = getConnection();
             Statement stmt = conn.createStatement()
        ) {
            String deleteSql = String.format("DELETE FROM users WHERE githubId = %d", userDTO.getGithubId());
            stmt.executeUpdate(deleteSql);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
