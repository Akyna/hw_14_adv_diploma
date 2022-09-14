package com.amboiko.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBService {
    private final String dbHost;
    private final String user;
    private final String password;


    public DBService(String dbHost, String user, String password) {
        this.dbHost = dbHost;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbHost, user, password);
    }
}
