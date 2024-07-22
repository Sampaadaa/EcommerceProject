package com.example.Ecommerce.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

//encapsulate the data for user login credentials, specifically the email and password

public class LoginCredentials {
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

}
