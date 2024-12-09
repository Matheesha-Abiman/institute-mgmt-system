package com.example.institute1.db;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/Institute";
    private final String USERNAME = "root";
    private final String PASSWORD = "Ijse@1234";

    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
    }

    public static DBConnection getInstance() throws ClassNotFoundException, SQLException {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        } else if (dbConnection.getConnection().isClosed()) {
            dbConnection.connection = DriverManager.getConnection(dbConnection.url, dbConnection.USERNAME, dbConnection.PASSWORD);
        }
        return dbConnection;
    }
}
