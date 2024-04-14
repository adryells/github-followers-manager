package com.wavers.server;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginManager extends QueryBase {
    public void insertLogin(String username, String accessToken) {
        try (
            Connection conn = getConnection();
             Statement stmt = conn.createStatement()
        ) {
            String insertSql = String.format(
                "INSERT INTO login (username, accessToken) VALUES ('%s', '%s')",
                username, accessToken
            );
            stmt.executeUpdate(insertSql);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM login");
             ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                String username = result.getString("username");
                usernames.add(username);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return usernames;
    }

    public String getAccessToken(String username) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT accessToken FROM login WHERE username = ?");
        ) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getString("accessToken");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }
}
