package com.example.institute1.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseDto {
    private String courseId;
    private String adminId;
    private String courseName;
    private String courseFee;
    private String courseDescription;
}
