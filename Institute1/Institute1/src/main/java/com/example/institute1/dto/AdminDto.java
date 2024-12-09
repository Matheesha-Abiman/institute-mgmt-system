package com.example.institute1.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminDto {

    private String adminId;
    private String adminName;
    private String adminEmail;
    private String adminPassword;
}
