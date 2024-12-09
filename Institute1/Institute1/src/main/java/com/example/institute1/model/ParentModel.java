package com.example.institute1.model;

import com.example.institute1.dto.ParentDto;
import com.example.institute1.crud.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParentModel {

    public static String getNextParentID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT parent_id FROM Parent ORDER BY parent_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int nextIndex = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("P%03d", nextIndex);
        }
        return "P001";
    }

    public static List<ParentDto> getAllParents() throws SQLException, ClassNotFoundException {
        List<ParentDto> parents = new ArrayList<>();
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM Parent");
        while (resultSet.next()) {
            parents.add(new ParentDto(
                    resultSet.getString("parent_id"),
                    resultSet.getString("admin_id"),
                    resultSet.getString("name"),
                    resultSet.getString("email")
            ));
        }
        return parents;
    }

    public static boolean saveParent(ParentDto parentDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO Parent (parent_id, admin_id, name, email) VALUES (?, ?, ?, ?)",
                parentDto.getParentId(), parentDto.getAdminId(), parentDto.getParentName(), parentDto.getParentEmail());
    }

    public static boolean updateParent(ParentDto parentDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE Parent SET admin_id = ?, name = ?, email = ? WHERE parent_id = ?",
                parentDto.getAdminId(), parentDto.getParentName(), parentDto.getParentEmail(), parentDto.getParentId());
    }

    public static boolean deleteParent(String parentId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM Parent WHERE parent_id = ?", parentId);
    }
}
