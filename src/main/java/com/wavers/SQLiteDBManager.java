package com.wavers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDBManager {
    private static final String DATABASE_URL = "jdbc:sqlite:user_data.db";

    public SQLiteDBManager() {
        createTable();
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT NOT NULL," +
                    "avatarUrl TEXT," +
                    "htmlUrl TEXT" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void insertUserData(UserData userData) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            String sql = String.format("INSERT INTO users (login, avatarUrl, htmlUrl) " +
                            "VALUES ('%s', '%s', '%s')",
                    userData.getLogin(), userData.getAvatarUrl(), userData.getHtmlUrl());
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}



