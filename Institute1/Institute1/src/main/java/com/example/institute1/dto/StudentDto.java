package com.example.institute1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private String studentId;
    private String adminId;
    private String parentId;
    private String name;
    private String address;
    private Date dob;
}
