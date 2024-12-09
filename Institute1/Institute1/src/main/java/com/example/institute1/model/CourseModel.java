package com.example.institute1.model;

import com.example.institute1.crud.CrudUtil;
import com.example.institute1.dto.CourseDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseModel {

    public static String getNextCourseID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT course_id FROM Course ORDER BY course_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int nextIndex = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("C%03d", nextIndex);
        }
        return "C001";
    }

    public static List<CourseDto> getAllCourses() throws SQLException, ClassNotFoundException {
        List<CourseDto> courses = new ArrayList<>();
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM Course");
        while (resultSet.next()) {
            courses.add(new CourseDto(
                    resultSet.getString("course_id"),
                    resultSet.getString("admin_id"),
                    resultSet.getString("name"),
                   resultSet.getString("fee"),
                    resultSet.getString("description")
            ));
        }
        return courses;
    }

    public static boolean saveCourse(CourseDto courseDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO Course (course_id, admin_id, name, fee, description) VALUES (?, ?, ?, ?, ?)",
                courseDto.getCourseId(), courseDto.getAdminId(), courseDto.getCourseName(), courseDto.getCourseFee(), courseDto.getCourseDescription());
    }

    public static boolean updateCourse(CourseDto courseDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE Course SET admin_id = ?, name = ?, fee = ?, description = ? WHERE course_id = ?",
                courseDto.getAdminId(), courseDto.getCourseName(), courseDto.getCourseFee(), courseDto.getCourseDescription(), courseDto.getCourseId());
    }


    public static boolean deleteCourse(String courseId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM Course WHERE course_id = ?", courseId);
    }

    public static CourseDto searchById(String selectCourseId) throws SQLException, ClassNotFoundException {
        ResultSet set = CrudUtil.execute("SELECT * FROM Course WHERE course_id=?",selectCourseId);

        if(set.next()){
            return new CourseDto(
                    set.getString(1),
                    set.getString(2),
                    set.getString(3),
                    set.getString(4),
                    set.getString(5)
            );
        }
        return null;
    }

    public static ArrayList<String> getAllCourseId() throws SQLException, ClassNotFoundException {
        ResultSet set = CrudUtil.execute("SELECT name From Course");

        ArrayList<String> courseIds = new ArrayList<>();
        while (set.next()){
            courseIds.add(set.getString(1));

        }
        return courseIds;
    }
}
