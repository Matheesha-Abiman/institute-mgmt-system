package com.example.institute1.model;

import com.example.institute1.crud.CrudUtil;
import com.example.institute1.dto.AdminDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminModel {

    public static String getNextAdminID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT admin_id FROM Admin ORDER BY admin_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int nextIndex = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("A%03d", nextIndex);
        }
        return "A001";
    }

    public static List<AdminDto> getAllAdmins() throws SQLException, ClassNotFoundException {
        List<AdminDto> admins = new ArrayList<>();
        ResultSet resultSet = CrudUtil.execute("SELECT admin_id, name, email, password FROM Admin");
        while (resultSet.next()) {
            admins.add(new AdminDto(
                    resultSet.getString("admin_id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("password")
            ));
        }
        return admins;
    }

    public static boolean saveAdmin(AdminDto adminDto) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO Admin (admin_id, name, email, password) VALUES (?, ?, ?, AES_ENCRYPT(?, 'your_secret_key'))";
        return CrudUtil.execute(query,
                adminDto.getAdminId(), adminDto.getAdminName(), adminDto.getAdminEmail(), adminDto.getAdminPassword());
    }

    public static boolean updateAdmin(AdminDto adminDto) throws SQLException, ClassNotFoundException {
        String query = "UPDATE Admin SET name = ?, email = ?, password = AES_ENCRYPT(?, 'your_secret_key') WHERE admin_id = ?";
        return CrudUtil.execute(query,
                adminDto.getAdminName(), adminDto.getAdminEmail(), adminDto.getAdminPassword(), adminDto.getAdminId());
    }

    public static boolean deleteAdmin(String adminId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM Admin WHERE admin_id = ?";
        return CrudUtil.execute(query, adminId);
    }
}
