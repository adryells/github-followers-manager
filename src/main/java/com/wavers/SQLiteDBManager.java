package com.wavers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDBManager {
    private static final String DATABASE_URL = "jdbc:sqlite:user_data.db";

    public SQLiteDBManager() {
        createTable();
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "login TEXT NOT NULL, " +
                    "avatarUrl TEXT, " +
                    "htmlUrl TEXT NOT NULL, " +
                    "githubId INTEGER NOT NULL" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro ao criar a tabela: " + e.getMessage());
        }
    }


    public void insertUserData(UserData userData) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            String checkSql = String.format("SELECT COUNT(*) FROM users WHERE githubId = %d", userData.getGithubId());
            ResultSet resultSet = stmt.executeQuery(checkSql);
            resultSet.next();
            int count = resultSet.getInt(1);
            resultSet.close();

            if (count == 0) {
                String insertSql = String.format(
                        "INSERT INTO users (login, avatarUrl, htmlUrl, githubId) VALUES ('%s', '%s', '%s', %d)",
                        userData.getLogin(), userData.getAvatarUrl(), userData.getHtmlUrl(), userData.getGithubId()
                );
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public List<UserData> getAllUsers() {
        List<UserData> userList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                String login = result.getString("login");
                String avatarUrl = result.getString("avatarUrl");
                String htmlUrl = result.getString("htmlUrl");
                long githubId = result.getLong("githubId");

                UserData userData = new UserData(githubId, login, avatarUrl, htmlUrl);
                userList.add(userData);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return userList;
    }

}



