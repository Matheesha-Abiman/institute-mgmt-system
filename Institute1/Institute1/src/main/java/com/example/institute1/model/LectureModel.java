package com.example.institute1.model;

import com.example.institute1.crud.CrudUtil;
import com.example.institute1.dto.AdminDto;
import com.example.institute1.dto.LectureDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LectureModel {

    public static String getNextLectureID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT lecture_id FROM Lecture ORDER BY lecture_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int nextIndex = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("L%03d", nextIndex);
        }
        return "L001";
    }

    public static List<LectureDto> getAllLectures() throws SQLException, ClassNotFoundException {
        List<LectureDto> lectures = new ArrayList<>();
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM Lecture");
        while (resultSet.next()) {
            lectures.add(new LectureDto(
                    resultSet.getString("lecture_id"),
                    resultSet.getString("module_id"),
                    resultSet.getString("name"),
                    resultSet.getString("email")
            ));
        }
        return lectures;
    }

    public static boolean saveLecture(LectureDto lectureDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO Lecture (lecture_id, module_id, name, email) VALUES (?, ?, ?, ?)",
                lectureDto.getLectureId(), lectureDto.getModuleId(), lectureDto.getLectureName(), lectureDto.getLectureEmail());
    }

    public static boolean updateLecture(LectureDto lectureDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE Lecture SET module_id = ?, name = ?, email = ? WHERE lecture_id = ?",
                lectureDto.getModuleId(), lectureDto.getLectureName(), lectureDto.getLectureEmail(), lectureDto.getLectureId());
    }

    public static boolean deleteLecture(String lectureId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM Lecture WHERE lecture_id = ?", lectureId);
    }
}
