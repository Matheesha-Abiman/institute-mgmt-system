package com.example.institute1.crud;

import com.example.institute1.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;

public class CrudUtil {

    // Execute SQL statements (SELECT, INSERT, UPDATE, DELETE)
    public static <T> T execute(String sql, Object... obj) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = connection.prepareStatement(sql);

        // Set the parameters in the SQL statement
        for (int i = 0; i < obj.length; i++) {
            pst.setObject(i + 1, obj[i]);
        }

        // If it's a SELECT query, execute and return the ResultSet
        if (sql.trim().toUpperCase().startsWith("SELECT")) {
            ResultSet resultSet = pst.executeQuery();
            return (T) resultSet;
        } else {
            // For INSERT, UPDATE, DELETE, execute the update and return true/false based on success
            int result = pst.executeUpdate();
            boolean isDone = result > 0;
            return (T) Boolean.valueOf(isDone);
        }
    }
}