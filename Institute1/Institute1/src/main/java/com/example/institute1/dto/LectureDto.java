package com.example.institute1.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class LectureDto {
    private String lectureId;
    private String moduleId;
    private String lectureName;
    private String lectureEmail;
}
