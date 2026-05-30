package com.example.userapi.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
    private AddressDto address;
    private CompanyDto company;
}
