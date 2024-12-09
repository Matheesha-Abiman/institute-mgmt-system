package com.example.institute1.model;

import com.example.institute1.crud.CrudUtil;
import com.example.institute1.dto.StaffDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffModel {

    public static String getNextStaffID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT staff_id FROM Staff ORDER BY staff_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int nextIndex = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("S%03d", nextIndex);
        }
        return "S001";
    }

    public static List<StaffDto> getAllStaff() throws SQLException, ClassNotFoundException {
        List<StaffDto> staffList = new ArrayList<>();
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM Staff");
        while (resultSet.next()) {
            staffList.add(new StaffDto(
                    resultSet.getString("staff_id"),
                    resultSet.getString("admin_id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("description"),
                    resultSet.getDouble("salary")
            ));
        }
        return staffList;
    }

    public static boolean saveStaff(StaffDto staffDto) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO Staff (staff_id, admin_id, name, email, description, salary) VALUES (?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(query,
                staffDto.getStaffId(), staffDto.getAdminId(), staffDto.getStaffName(), staffDto.getEmail(), staffDto.getDescription(), staffDto.getSalary());
    }

    public static boolean updateStaff(StaffDto staffDto) throws SQLException, ClassNotFoundException {
        String query = "UPDATE Staff SET admin_id = ?, name = ?, email = ?, description = ?, salary = ? WHERE staff_id = ?";
        return CrudUtil.execute(query,
                staffDto.getAdminId(), staffDto.getStaffName(), staffDto.getEmail(), staffDto.getDescription(), staffDto.getSalary(), staffDto.getStaffId());
    }

    public static boolean deleteStaff(String staffId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM Staff WHERE staff_id = ?";
        return CrudUtil.execute(query, staffId);
    }
}
