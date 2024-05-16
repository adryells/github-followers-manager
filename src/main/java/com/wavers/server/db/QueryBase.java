package com.wavers.server.db;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryBase {
    SQLiteDriver dbManager = SQLiteDriver.getInstance();

    public Connection getConnection() throws SQLException {
        return dbManager.getConnection();
    }
}
