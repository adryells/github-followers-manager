package com.wavers.server;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryBase {
    SQLiteDriver dbManager = SQLiteDriver.getInstance();

    public Connection getConnection() throws SQLException {
        return dbManager.getConnection();
    }
}
