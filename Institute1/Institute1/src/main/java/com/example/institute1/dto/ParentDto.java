package com.example.institute1.dto;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ParentDto {
    private String parentId;
    private String adminId;
    private String parentName;
    private String parentEmail;

}
