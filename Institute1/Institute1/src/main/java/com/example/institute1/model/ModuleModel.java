package com.example.institute1.model;

import com.example.institute1.crud.CrudUtil;
import com.example.institute1.dto.ModuleDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModuleModel {


    public static String getNextModuleID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT module_id FROM Module ORDER BY module_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int nextIndex = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("M%03d", nextIndex);
        }
        return "M001";
    }


    public static List<ModuleDto> getAllModules() throws SQLException, ClassNotFoundException {
        List<ModuleDto> modules = new ArrayList<>();
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM Module");
        while (resultSet.next()) {
            modules.add(new ModuleDto(
                    resultSet.getString("module_id"),
                    resultSet.getString("course_id"),
                    resultSet.getString("module_name")
            ));
        }
        return modules;
    }

    public static boolean saveModule(ModuleDto moduleDto) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO Module (module_id, course_id, module_name) VALUES (?, ?, ?)";
        return CrudUtil.execute(query,
                moduleDto.getModuleId(), moduleDto.getCourseId(), moduleDto.getModuleName());
    }

    public static boolean updateModule(ModuleDto moduleDto) throws SQLException, ClassNotFoundException {
        String query = "UPDATE Module SET course_id = ?, module_name = ? WHERE module_id = ?";
        return CrudUtil.execute(query,
                moduleDto.getCourseId(), moduleDto.getModuleName(), moduleDto.getModuleId());
    }

    public static boolean deleteModule(String moduleId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM Module WHERE module_id = ?";
        return CrudUtil.execute(query, moduleId);
    }
}
