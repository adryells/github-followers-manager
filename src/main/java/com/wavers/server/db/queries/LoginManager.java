package com.wavers.server.db.queries;


import com.wavers.server.db.QueryBase;
import com.wavers.server.utils.ResultSetStream;

import java.sql.*;
import java.util.Collections;
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

    public List<String> getAllLoggedUsers() {
        try (
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT username FROM login");
            ResultSet result = stmt.executeQuery()
        ) {
            return ResultSetStream
                .toStream(result)
                .map(rs -> {
                    try {
                        return rs.getString("username");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        } catch (RuntimeException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public String getAccessToken(String username) {
        try (
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT accessToken FROM login WHERE username = ?")
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
