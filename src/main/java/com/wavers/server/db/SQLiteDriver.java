package com.wavers.server.db;

import java.sql.*;

public class SQLiteDriver {
    private static final String DATABASE_URL = "jdbc:sqlite:user_data.db";
    private static SQLiteDriver instance;

    private SQLiteDriver() {
        createTable();
    }

    // Singleton instance of DB
    public static SQLiteDriver getInstance() {
        if (instance == null) {
            instance = new SQLiteDriver();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    private void createTable() {
        try (
            Connection conn = getConnection();
             Statement stmt = conn.createStatement()
        ) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "login TEXT NOT NULL, " +
                    "avatarUrl TEXT, " +
                    "htmlUrl TEXT NOT NULL, " +
                    "githubId INTEGER NOT NULL" +
                    ")";
            stmt.execute(sql);

            String loginTableSql = "CREATE TABLE IF NOT EXISTS login (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "accessToken TEXT NOT NULL" +
                    ")";
            stmt.execute(loginTableSql);
        } catch (SQLException e) {
            System.err.println("Erro ao criar a tabela: " + e.getMessage());
        }
    }
}
